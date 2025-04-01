package com.application.genzhouse.ui.welcome.homesearch

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.application.genzhouse.databinding.ActivityCitySelectionBinding
import com.application.genzhouse.ui.userhomepage.RoomListing

class CitySelection : AppCompatActivity() {
    lateinit var binding: ActivityCitySelectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initOnClick()
    }

    private fun initOnClick() {
        binding.apply {
            btnSearch.setOnClickListener {
                startActivity(Intent(this@CitySelection, RoomListing::class.java))
            }
        }
    }

    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}