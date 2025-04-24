package com.application.genzhouse.ui.welcome.homesearch

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.application.genzhouse.databinding.ActivityChooseCategoryBinding

class ChooseCategory : AppCompatActivity() {
    lateinit var binding: ActivityChooseCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
        initOnclick()
    }

    private fun initOnclick() {
        binding.apply {
            pgCard.setOnClickListener{
                startActivity(Intent(this@ChooseCategory,CitySelection::class.java))
            }
            rentCard.setOnClickListener{
                startActivity(Intent(this@ChooseCategory,CitySelection::class.java))
            }
            buyCard.setOnClickListener{
                startActivity(Intent(this@ChooseCategory,CitySelection::class.java))
            }
        }
    }

    private fun initUi() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}