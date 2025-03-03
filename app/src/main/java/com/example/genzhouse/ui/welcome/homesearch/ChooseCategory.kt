package com.example.genzhouse.ui.welcome.homesearch

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.genzhouse.R
import com.example.genzhouse.databinding.ActivityChooseCategoryBinding

class ChooseCategory : AppCompatActivity() {
    lateinit var binding: ActivityChooseCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        binding.pgCard.setOnClickListener{
            startActivity(Intent(this,CitySelection::class.java))
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