package com.example.genzhouse.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.genzhouse.MainActivity
import com.example.genzhouse.R
import com.example.genzhouse.databinding.ActivityWelcomeBinding
import com.example.genzhouse.ui.welcome.homesearch.ChooseCategory
import com.example.genzhouse.ui.welcome.sellrentproperty.SellRentProperty
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class WelcomeActivity : AppCompatActivity() {
    lateinit var welcomeBinding: ActivityWelcomeBinding
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

        welcomeBinding.sellRentCard.setOnClickListener {
            startActivity(Intent(this,
                SellRentProperty::class.java
            ))
        }
        welcomeBinding.homeSearchCard.setOnClickListener{
            startActivity(Intent(this,ChooseCategory::class.java
            ))
        }
    }
}