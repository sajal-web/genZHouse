package com.example.genzhouse.ui.welcome

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.genzhouse.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                runBlocking {
                    delay(1000)
                    false
                }
            }
        }
        setContentView(R.layout.activity_welcome)
    }
}