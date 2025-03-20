package com.application.genzhouse.ui.welcome.homesearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.application.genzhouse.databinding.ActivityCitySelectionBinding

class CitySelection : AppCompatActivity() {
    lateinit var binding: ActivityCitySelectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
    }
    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}