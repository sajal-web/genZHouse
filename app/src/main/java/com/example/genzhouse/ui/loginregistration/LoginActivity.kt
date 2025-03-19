package com.example.genzhouse.ui.loginregistration

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.genzhouse.R
import com.example.genzhouse.databinding.ActivityLoginBinding
import com.example.genzhouse.databinding.ActivityOtpVerificationBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var activityLoginBinding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(activityLoginBinding.root)

        activityLoginBinding.sendOtpBtn.setOnClickListener{
            startActivity(Intent(this,OtpVerificationActivity::class.java))
        }

    }
}