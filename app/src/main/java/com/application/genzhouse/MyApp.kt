package com.application.genzhouse

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class MyApp : Application() {
    private lateinit var auth: FirebaseAuth
    private val appScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isInitializedForUser = false
    private var tokenRefreshJob: Job? = null
    @Volatile private var isRefreshingToken = false

    companion object {
        private const val TAG = "TokenManager"
        private const val TOKEN_REFRESH_INTERVAL = 30 * 60 * 1000L // 30 minutes
        private const val RETRY_INTERVAL = 5 * 60 * 1000L // 5 minutes
        private const val TOKEN_FRESH_THRESHOLD = 5 * 60 * 1000L // 5 minutes
        private const val PREFS_NAME = "AppPrefs"
        private const val KEY_FIREBASE_TOKEN = "firebase_token"
        private const val KEY_TOKEN_TIMESTAMP = "token_timestamp"

        fun get(context: Context): MyApp = context.applicationContext as MyApp
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        setupAuthStateListener()
    }

    override fun onTerminate() {
        cleanupForLogout()
        appScope.cancel()
        super.onTerminate()
    }

    private fun setupAuthStateListener() {
        auth.addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.let { user ->
                if (!isInitializedForUser) {
                    initializeForUser(user)
                }
            } ?: run {
                if (isInitializedForUser) {
                    cleanupForLogout()
                }
            }
        }
    }

    private fun initializeForUser(user: FirebaseUser) {
        Log.d(TAG, "Initializing for user: ${user.uid}")
        isInitializedForUser = true
        startTokenRefreshSystem()
        TokenRefreshWorker.scheduleRefresh(this)
    }

    private fun cleanupForLogout() {
        Log.d(TAG, "Cleaning up for logout")
        isInitializedForUser = false
        stopTokenRefreshSystem()
        cancelTokenRefreshWork()
        clearAllSharedPreferences()
    }

    private fun startTokenRefreshSystem() {
        if (!isInitializedForUser) return

        stopTokenRefreshSystem() // cancel any existing job

        refreshFirebaseToken()

        tokenRefreshJob = appScope.launch {
            while (isActive && isInitializedForUser) {
                delay(TOKEN_REFRESH_INTERVAL)
                refreshFirebaseToken()
            }
        }
    }

    private fun stopTokenRefreshSystem() {
        tokenRefreshJob?.cancel()
        tokenRefreshJob = null
    }

    private fun refreshFirebaseToken() {
        if (!isInitializedForUser || isRefreshingToken) return

        appScope.launch {
            isRefreshingToken = true
            try {
                val user = auth.currentUser ?: return@launch
                val result = user.getIdToken(true).await()
                result.token?.let { token ->
                    storeFirebaseToken(token)
                    Log.d(TAG, "Token refreshed successfully")
                } ?: run {
                    Log.e(TAG, "Token was null")
                    scheduleRetryRefresh()
                }
            } catch (e: FirebaseAuthException) {
                handleFirebaseAuthError(e)
            } catch (e: Exception) {
                Log.e(TAG, "Token refresh failed", e)
                scheduleRetryRefresh()
            } finally {
                isRefreshingToken = false
            }
        }
    }

    private fun handleFirebaseAuthError(exception: FirebaseAuthException) {
        Log.e(TAG, "Auth error: ${exception.errorCode}", exception)
        when (exception.errorCode) {
            "ERROR_INVALID_CREDENTIAL" -> {
                Log.w(TAG, "Invalid credentials - forcing logout")
                auth.signOut()
            }
            else -> scheduleRetryRefresh()
        }
    }

    private fun scheduleRetryRefresh() {
        if (!isInitializedForUser) return

        appScope.launch {
            delay(RETRY_INTERVAL)
            if (isInitializedForUser) {
                refreshFirebaseToken()
            }
        }
    }

    private fun storeFirebaseToken(token: String) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
            putString(KEY_FIREBASE_TOKEN, token)
            putLong(KEY_TOKEN_TIMESTAMP, System.currentTimeMillis())
            apply()
        }
    }

    private fun clearAllSharedPreferences() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }

    private fun cancelTokenRefreshWork() {
        WorkManager.getInstance(this).cancelUniqueWork(TokenRefreshWorker.WORK_NAME)
    }

    fun getCurrentToken(callback: (String?) -> Unit) {
        if (!isInitializedForUser) {
            callback(null)
            return
        }

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedToken = prefs.getString(KEY_FIREBASE_TOKEN, null)
        val tokenTimestamp = prefs.getLong(KEY_TOKEN_TIMESTAMP, 0)
        val isTokenFresh = System.currentTimeMillis() - tokenTimestamp < TOKEN_FRESH_THRESHOLD

        if (storedToken != null && isTokenFresh) {
            callback(storedToken)
            return
        }

        appScope.launch {
            try {
                val user = auth.currentUser
                if (user != null) {
                    val result = user.getIdToken(true).await()
                    result.token?.let { token ->
                        storeFirebaseToken(token)
                        withContext(Dispatchers.Main) {
                            callback(token)
                        }
                    } ?: withContext(Dispatchers.Main) {
                        callback(storedToken)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        callback(null)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting token", e)
                withContext(Dispatchers.Main) {
                    callback(storedToken)
                }
            }
        }
    }

    class TokenRefreshWorker(
        context: Context,
        params: WorkerParameters
    ) : CoroutineWorker(context, params) {

        override suspend fun doWork(): Result {
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser ?: return Result.success()

            return try {
                val token = user.getIdToken(true).await().token

                if (token != null) {
                    applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
                        putString(KEY_FIREBASE_TOKEN, token)
                        putLong(KEY_TOKEN_TIMESTAMP, System.currentTimeMillis())
                        apply()
                    }
                    Result.success()
                } else {
                    Result.retry()
                }
            } catch (e: FirebaseAuthException) {
                if (e.errorCode == "ERROR_INVALID_CREDENTIAL") {
                    auth.signOut()
                    Result.success()
                } else {
                    Result.retry()
                }
            } catch (e: Exception) {
                Result.retry()
            }
        }

        companion object {
            const val WORK_NAME = "TokenRefresh"

            fun scheduleRefresh(context: Context) {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val request = PeriodicWorkRequestBuilder<TokenRefreshWorker>(
                    6, TimeUnit.HOURS
                )
                    .setConstraints(constraints)
                    .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        15, TimeUnit.MINUTES
                    )
                    .build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
            }
        }
    }

    fun logout() {
        auth.signOut()
    }
}
