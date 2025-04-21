package com.application.genzhouse

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.application.genzhouse.databinding.ActivityWelcomeBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.OwnerDashBoard
import com.application.genzhouse.ui.loginregistration.LoginActivity
import com.application.genzhouse.ui.welcome.homesearch.ChooseCategory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class WelcomeActivity : AppCompatActivity() {
    private lateinit var welcomeBinding: ActivityWelcomeBinding
    private val auth = FirebaseAuth.getInstance()
    private var backPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            false // Let it proceed immediately
        }

        welcomeBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(welcomeBinding.root)

        // Delay anything you want after layout is set — like showing content, animation, etc.
        Handler(Looper.getMainLooper()).postDelayed({
            initUI()
            initObserver()
            onClick()
        }, 1000) // Optional: keep delay only if needed for visuals
    }

    private fun initUI() {

    }

    private fun initObserver() {

    }

    private fun onClick() {
        // Handle double back press (Toast first, then Dialog)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    showExitConfirmationDialog() // Show confirmation dialog
                } else {
                    backPressedOnce = true
                    Toast.makeText(this@WelcomeActivity, "Press again to exit", Toast.LENGTH_SHORT).show()

                    // Reset `backPressedOnce` after 2 seconds
                    Handler(Looper.getMainLooper()).postDelayed({
                        backPressedOnce = false
                    }, 2000)
                }
            }
        })

        welcomeBinding.apply {
            sellRentCard.setOnClickListener {

                val uploadedRoomCount =
                    getSharedPreferences("UserProfile", MODE_PRIVATE).getInt("total_rooms", 0)
                if (auth.currentUser != null) {
                    if (uploadedRoomCount == 0) {
                        startActivity(
                            Intent(
                                this@WelcomeActivity,
                                LoginActivity::class.java
                            )
                        )
                    } else {
                        startActivity(
                            Intent(
                                this@WelcomeActivity,
                                OwnerDashBoard::class.java
                            )
                        )
                        finish()
                    }
                } else {
                    startActivity(
                        Intent(
                            this@WelcomeActivity,
                            LoginActivity::class.java
                        )
                    )
                }
            }
            homeSearchCard.setOnClickListener {
                startActivity(
                    Intent(
                        this@WelcomeActivity, ChooseCategory::class.java
                    )
                )
            }
        }
    }


    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit Application")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                finishAffinity() // Clears the entire activity stack and exits the app
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}