package com.application.genzhouse.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.application.genzhouse.databinding.ActivityWelcomeBinding
import com.application.genzhouse.ui.loginregistration.LoginActivity
import com.application.genzhouse.ui.welcome.homesearch.ChooseCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class WelcomeActivity : AppCompatActivity() {
    private lateinit var welcomeBinding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        welcomeBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                runBlocking {
                    delay(1000)
                    false
                }
            }
        }
        setContentView(welcomeBinding.root)
        initUI()
        initObserver()
        onClick()
    }

    private fun initUI() {

    }

    private fun initObserver() {

    }

    private fun onClick() {
        welcomeBinding.apply {
            sellRentCard.setOnClickListener {
                startActivity(
                    Intent(
                        this@WelcomeActivity,
                        LoginActivity::class.java
                    )
                )
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
}