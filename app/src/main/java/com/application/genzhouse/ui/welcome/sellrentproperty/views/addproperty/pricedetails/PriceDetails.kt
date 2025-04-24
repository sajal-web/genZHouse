package com.application.genzhouse.ui.welcome.sellrentproperty.views.addproperty.pricedetails

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.genzhouse.R
import com.application.genzhouse.databinding.ActivityPriceDetailsBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.views.addproperty.uploadphotos.UploadProperty
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PriceDetails : AppCompatActivity() {

    private lateinit var binding: ActivityPriceDetailsBinding
    private val calendar = Calendar.getInstance()
    private var selectedCard: MaterialCardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPriceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initOnClick()
        setupCardClickListeners()
        setupDatePicker()
    }

    private fun initOnClick() {
        binding.postPropertyButton.setOnClickListener {
            // Get all the input values from this activity
            val monthlyRentText = binding.monthlyRentEditText.text.toString().trim()
            val availableFrom = binding.availableFromEditText.text.toString().trim()

            // Convert to Double with null safety
            val monthlyRent = monthlyRentText.toDoubleOrNull() ?: 0.0

            // Determine security deposit value as Double
            val securityDeposit = when (selectedCard) {
                binding.noneDepositCard -> 0.0
                binding.oneMonthDepositCard -> monthlyRent // 1 month rent
                binding.towMonthDepositCard -> monthlyRent * 2 // 2 months rent
                binding.customDepositCard -> binding.customRent.text.toString().trim().toDoubleOrNull() ?: 0.0
                else -> 0.0 // Default case
            }

            // Validate required fields
            if (monthlyRentText.isEmpty() || availableFrom.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate numeric values
            if (monthlyRent <= 0) {
                Toast.makeText(this, "Please enter a valid rent amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create intent and put all extras
            val intent = Intent(this, UploadProperty::class.java).apply {
                // Previous activity data
                putExtra("NAME", intent.getStringExtra("NAME"))
                putExtra("ROLE", intent.getStringExtra("ROLE"))
                putExtra("ROOM_TYPE", intent.getStringExtra("ROOM_TYPE"))
                putExtra("LOCATION", intent.getStringExtra("LOCATION"))
                putExtra("BUILDING", intent.getStringExtra("BUILDING"))
                putExtra("LOCALITY", intent.getStringExtra("LOCALITY"))
                putExtra("AREA", intent.getDoubleExtra("AREA", 0.0))
                putExtra("FURNISHED_TYPE", intent.getStringExtra("FURNISHED_TYPE"))
                putExtra("BHK", intent.getStringExtra("BHK"))
                putExtra("PROPERTY_TYPE", intent.getStringExtra("PROPERTY_TYPE"))

                // New fields from this activity as Double
                putExtra("MONTHLY_RENT", monthlyRent)
                putExtra("AVAILABLE_FROM", availableFrom)
                putExtra("SECURITY_DEPOSIT", securityDeposit)
            }

            startActivity(intent)
        }
    }

    private fun setupCardClickListeners() {
        val cards = listOf(
            binding.noneDepositCard,
            binding.oneMonthDepositCard,
            binding.towMonthDepositCard,
            binding.customDepositCard
        )

        cards.forEach { card ->
            card.setOnClickListener {
                handleCardSelection(card)
            }
        }

        // Custom card has additional logic
        binding.customDepositCard.setOnClickListener {
            handleCardSelection(binding.customDepositCard)
        }
    }

    private fun handleCardSelection(card: MaterialCardView) {
        // Deselect the previously selected card
        selectedCard?.let { deselectCard(it) }

        // Select the new card
        selectCard(card)
        selectedCard = card
        if (selectedCard == binding.customDepositCard) {
            toggleCustomAmountVisibility()
        } else {
            binding.customAmountEdittext.visibility = View.GONE
        }

        // Show a toast indicating which card is selected
        val cardText = (card.getChildAt(0) as? TextView)?.text?.toString() ?: "Custom"
        Toast.makeText(this, "$cardText selected", Toast.LENGTH_SHORT).show()
    }

    private fun selectCard(card: MaterialCardView) {
        card.apply {
            strokeWidth = 2
            strokeColor = getColor(R.color.colorPrimary)
        }
    }

    private fun deselectCard(card: MaterialCardView) {
        card.apply {
            strokeWidth = 0
            strokeColor = getColor(R.color.colorSecondary)
        }
    }

    private fun toggleCustomAmountVisibility() {
        binding.customAmountEdittext.visibility = if (binding.customAmountEdittext.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun setupDatePicker() {
        binding.availableFromEditText.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            // Set minimum date to today
            datePicker.minDate = System.currentTimeMillis() - 1000
            show()
        }
    }

    private fun updateDateInView() {
        val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        binding.availableFromEditText.setText(dateFormatter.format(calendar.time))

    }
    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}