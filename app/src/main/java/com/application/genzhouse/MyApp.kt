package com.application.genzhouse

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.work.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MyApp : Application() {
    private lateinit var auth: FirebaseAuth
    private var tokenRefreshTimer: CountDownTimer? = null
    private val appScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    companion object {
        private const val TAG = "TokenManager"
        private const val TOKEN_REFRESH_INTERVAL = 30 * 60 * 1000L // 30 minutes
        private const val RETRY_INTERVAL = 5 * 60 * 1000L // 5 minutes
        private const val TOKEN_FRESH_THRESHOLD = 5 * 60 * 1000L // 5 minutes
        private const val PREFS_NAME = "AppPrefs"
        private const val KEY_FIREBASE_TOKEN = "firebase_token"
        private const val KEY_TOKEN_TIMESTAMP = "token_timestamp"
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        setupAuthStateListener()
    }

    override fun onTerminate() {
        stopTokenRefreshSystem()
        appScope.cancel()
        super.onTerminate()
    }

    private fun setupAuthStateListener() {
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                startTokenRefreshSystem()
            } else {
                stopTokenRefreshSystem()
            }
        }
    }

    private fun startTokenRefreshSystem() {
        stopTokenRefreshSystem()

        // Immediate refresh
        refreshFirebaseToken()

        // Setup periodic refresh with CountDownTimer (more efficient than Handler)
        tokenRefreshTimer = object : CountDownTimer(TOKEN_REFRESH_INTERVAL, TOKEN_REFRESH_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) { /* Not used */ }

            override fun onFinish() {
                if (auth.currentUser != null) {
                    refreshFirebaseToken()
                    this.start() // Restart timer for next interval
                }
            }
        }.start()

        // Setup WorkManager as backup for when app is in background
        TokenRefreshWorker.scheduleRefresh(this)
    }

    private fun stopTokenRefreshSystem() {
        tokenRefreshTimer?.cancel()
        tokenRefreshTimer = null
    }

    private fun refreshFirebaseToken() {
        val user = auth.currentUser ?: return

        appScope.launch {
            try {
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
        appScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    Thread.sleep(RETRY_INTERVAL)
                    if (auth.currentUser != null) {
                        refreshFirebaseToken()
                    }
                } catch (e: InterruptedException) {
                    // Timer was canceled
                }
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

    fun getCurrentToken(callback: (String?) -> Unit) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedToken = prefs.getString(KEY_FIREBASE_TOKEN, null)
        val tokenTimestamp = prefs.getLong(KEY_TOKEN_TIMESTAMP, 0)
        val isTokenFresh = System.currentTimeMillis() - tokenTimestamp < TOKEN_FRESH_THRESHOLD

        if (storedToken != null && isTokenFresh) {
            callback(storedToken)
            return
        }

        // Force refresh if token is expired or missing
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
                        callback(storedToken) // Fallback to stored token
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        callback(null) // No user logged in
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting token", e)
                withContext(Dispatchers.Main) {
                    callback(storedToken) // Fallback to stored token on error
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
            val user = auth.currentUser ?: return Result.success() // No user to refresh

            return try {
                val token = user.getIdToken(true).await().token

                if (token != null) {
                    // Store token in SharedPreferences
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
                    Result.success() // No need to retry after logout
                } else {
                    Result.retry()
                }
            } catch (e: Exception) {
                Result.retry()
            }
        }

        companion object {
            private const val PREFS_NAME = "AppPrefs"
            private const val KEY_FIREBASE_TOKEN = "firebase_token"
            private const val KEY_TOKEN_TIMESTAMP = "token_timestamp"
            private const val WORK_NAME = "TokenRefresh"

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
}