package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.application.genzhouse.WelcomeActivity
import com.application.genzhouse.databinding.ActivityOwnerDashBoardBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.views.addproperty.SellRentPropertyForm
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.ownerlistings.OwnerListing
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.ownerprofile.ProfileActivity

class OwnerDashBoard : AppCompatActivity() {
    private lateinit var binding: ActivityOwnerDashBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initOnclick()
    }

    private fun initOnclick() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@OwnerDashBoard, WelcomeActivity::class.java))
                finish()
            }
        })
        binding.apply {
            ivOwnerProfile.setOnClickListener {
                startActivity(Intent(this@OwnerDashBoard, ProfileActivity::class.java))
            }
            viewAllRoom.setOnClickListener {
                startActivity(Intent(this@OwnerDashBoard,OwnerListing::class.java))
            }

            ownerAddRoom.setOnClickListener {
                startActivity(Intent(this@OwnerDashBoard,SellRentPropertyForm::class.java))
            }
        }

    }
}