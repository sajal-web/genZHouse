package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.ownerprofile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.application.genzhouse.R
import com.application.genzhouse.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()

    }
    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}