package com.example.genzhouse.ui.welcome.sellrentproperty

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.genzhouse.R
import com.example.genzhouse.databinding.ActivitySellRentPropertyBinding
import com.example.genzhouse.databinding.ActivityWelcomeBinding
import com.example.genzhouse.ui.welcome.propertydetails.PropertyDetails

class SellRentProperty : AppCompatActivity() {
    private lateinit var activitySellRentPropertyBinding: ActivitySellRentPropertyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySellRentPropertyBinding = ActivitySellRentPropertyBinding.inflate(layoutInflater)
        setContentView(activitySellRentPropertyBinding.root)
       initUI()
    }
    private fun initUI(){
        val rentBtn = activitySellRentPropertyBinding.rentButton
        val sellBtn = activitySellRentPropertyBinding.sellButton
        val pgBtn = activitySellRentPropertyBinding.pgButton
        rentBtn.isSelected = true
        val buttons = listOf(rentBtn,sellBtn,pgBtn)

        buttons.forEach{ button ->
            button.setOnClickListener {
                if (button.isSelected) {
                    // Deselect the button if it's already selected
                    button.isSelected = false
                } else {
                    // Deselect all buttons
                    buttons.forEach { it.isSelected = false }
                    // Select the clicked button
                    button.isSelected = true
                }
            }
        }

        activitySellRentPropertyBinding.backButton.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        activitySellRentPropertyBinding.nextButton.setOnClickListener {
            startActivity(Intent(this, PropertyDetails::class.java))
        }
    }
}