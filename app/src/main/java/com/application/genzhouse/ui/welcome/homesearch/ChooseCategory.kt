package com.application.genzhouse.ui.welcome.homesearch

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.application.genzhouse.databinding.ActivityChooseCategoryBinding
import com.application.genzhouse.ui.userhomepage.RoomDetails

class ChooseCategory : AppCompatActivity() {
    lateinit var binding: ActivityChooseCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        binding.pgCard.setOnClickListener{
            startActivity(Intent(this,RoomDetails::class.java))
        }
        binding.rentCard.setOnClickListener{
            startActivity(Intent(this,CitySelection::class.java))
        }
        binding.buyCard.setOnClickListener{
            startActivity(Intent(this,CitySelection::class.java))
        }


    }
    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}