package com.example.genzhouse.ui.welcome.sellrentproperty.propertydetails

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.genzhouse.R
import com.example.genzhouse.databinding.ActivityPropertyDetailsBinding
import com.example.genzhouse.ui.welcome.sellrentproperty.pricedetails.PriceDetails
import com.google.android.material.card.MaterialCardView

class PropertyDetails : AppCompatActivity() {
    private lateinit var binding: ActivityPropertyDetailsBinding
    // Store selected cards with their types
    private val selectedCards = mutableMapOf<String, MaterialCardView?>()
    // Define card types as an enum for better type safety
    private enum class CardType(val key: String) {
        PROPERTY("PROPERTY_TYPE"),
        BHK("BHK_TYPE"),
        FURNISH("FURNISH_TYPE")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPropertyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        setupPropertyTypeCards()
        setupBhkCards()
        setupFurnishTypeCards()
        // Set default selection
        selectCard(binding.cardIndependent, CardType.PROPERTY)

        binding.nextButton.setOnClickListener{
            startActivity(Intent(this, PriceDetails::class.java))
        }
    }

    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupPropertyTypeCards() {
        val propertyCards = listOf(
            binding.cardApartment,
            binding.cardIndependentFloor,
            binding.cardIndependent,
            binding.cardVilla
        )
        setupCardGroup(propertyCards, CardType.PROPERTY)
    }

    private fun setupBhkCards() {
        val bhkCards = listOf(
            binding.oneRk,
            binding.oneBhk,
            binding.twoBhk,
            binding.threeBhk,
            binding.fourBhk,
            binding.fiveBhk,
            binding.onepfiveBhk,
            binding.twopfiveBhk,
            binding.threepfiveBhk,
            binding.fourpfiveBhk
        )

        setupCardGroup(bhkCards, CardType.BHK)
    }

    private fun setupFurnishTypeCards() {
        val furnishCards = listOf(
            binding.fullyFurnished,
            binding.semiFurnished,
            binding.unfurnished
        )

        setupCardGroup(furnishCards, CardType.FURNISH)
    }

    private fun setupCardGroup(cards: List<MaterialCardView>, type: CardType) {
        cards.forEach { card ->
            card.setOnClickListener {
                selectCard(it as MaterialCardView, type)
            }
        }
    }

    private fun selectCard(card: MaterialCardView, type: CardType) {
        // Deselect the previously selected card
        selectedCards[type.key]?.apply {
            strokeWidth = 0
        }

        // Select the new card
        card.apply {
            strokeWidth = 2
            strokeColor = getColor(R.color.colorPrimary)
        }

        // Update the selected card
        selectedCards[type.key] = card

        // Show toast with the selected card's text
        showSelectionToast(card, type)
    }

    private fun showSelectionToast(card: MaterialCardView, type: CardType) {
        // Find the TextView within the card
        val textView = findTextViewInCard(card)
        val text = textView?.text?.toString() ?: "Unknown selection"

        // Create a message based on the selection type
        val message = when (type) {
            CardType.PROPERTY -> "Property type selected: $text"
            CardType.BHK -> "BHK type selected: $text"
            CardType.FURNISH -> "Furnishing type selected: $text"
        }

        // Show the toast
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun findTextViewInCard(card: MaterialCardView): TextView? {
        // Find the first TextView child of the card
        // Note: This assumes the MaterialCardView has a direct TextView child or within a simple layout

        // If the MaterialCardView contains only one TextView directly
        if (card.childCount == 1 && card.getChildAt(0) is TextView) {
            return card.getChildAt(0) as TextView
        }

        // Search for the first TextView recursively in the view hierarchy
        return findTextViewInViewRecursively(card)
    }

    private fun findTextViewInViewRecursively(view: android.view.View): TextView? {
        // If the view is a TextView, return it
        if (view is TextView) {
            return view
        }

        // If the view is a ViewGroup, search its children
        if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                val childView = view.getChildAt(i)
                val textView = findTextViewInViewRecursively(childView)
                if (textView != null) {
                    return textView
                }
            }
        }

        return null
    }
}