package com.example.genzhouse.ui.welcome.sellrentproperty.pricedetails

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.genzhouse.R
import com.example.genzhouse.databinding.ActivityPriceDetailsBinding
import com.example.genzhouse.ui.welcome.sellrentproperty.uploadlisting.UploadProperty
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PriceDetails : AppCompatActivity() {

    private lateinit var binding: ActivityPriceDetailsBinding
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private var selectedCard: MaterialCardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPriceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        binding.postPropertyButton.setOnClickListener{
            startActivity(Intent(this,UploadProperty::class.java))
        }
        setupCardClickListeners()
        setupDatePicker()
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
        binding.availableFromEditText.setText(dateFormatter.format(calendar.time))
    }
    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}