package com.example.genzhouse.ui.loginregistration

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.genzhouse.R
import com.example.genzhouse.databinding.ActivityOtpVerificationBinding
import com.example.genzhouse.ui.welcome.sellrentproperty.SellRentProperty

class OtpVerificationActivity : AppCompatActivity() {
    private lateinit var activityOtpVerificationBinding: ActivityOtpVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityOtpVerificationBinding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(activityOtpVerificationBinding.root)

        activityOtpVerificationBinding.verifyOtpBtn.setOnClickListener{
            startActivity(Intent(this,SellRentProperty::class.java))
        }
    }
}