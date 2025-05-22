package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty.fragments

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.application.genzhouse.R
import com.application.genzhouse.databinding.FragmentPropertyDetailsBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.RoomDetails
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.RoomItem
import com.google.android.material.card.MaterialCardView

class PropertyDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPropertyDetailsBinding
    private lateinit var roomItem: RoomItem
    private val selectedCards = mutableMapOf<String, MaterialCardView?>()

    private enum class CardType(val key: String) {
        PROPERTY("PROPERTY_TYPE"),
        BHK("BHK_TYPE"),
        FURNISH("FURNISH_TYPE")
    }

    companion object {
        private const val ARG_ROOM_ITEM = "room_item"

        fun newInstance(room: RoomItem): PropertyDetailsFragment {
            val fragment = PropertyDetailsFragment()
            val args = Bundle().apply {
                putParcelable(ARG_ROOM_ITEM, room)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            roomItem = it.getParcelable(ARG_ROOM_ITEM)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPropertyDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initOnClick()
    }

    private fun initUi() {
        setupToolbar()
        binding.apply {
            // Set basic property details
            buildingEditText.setText(roomItem.details.type)
            localityEditText.setText(roomItem.details.location)
            areaEditText.setText(roomItem.details.area.toString())
            shareCheckBox.isChecked = false

            // Initialize card groups
            setupPropertyTypeCards()
            setupBhkCards()
            setupFurnishTypeCards()

            // Set initial selections based on roomItem
            setInitialSelections()
        }
    }

    private fun setInitialSelections() {
        // Set property type selection
        when (roomItem.details.propertyType.lowercase()) {
            "apartment" -> selectCard(binding.cardApartment, CardType.PROPERTY)
            "independent floor" -> selectCard(binding.cardIndependentFloor, CardType.PROPERTY)
            "independent house" -> selectCard(binding.cardIndependent, CardType.PROPERTY)
            "villa" -> selectCard(binding.cardVilla, CardType.PROPERTY)
        }

        // Set BHK selection
        when (roomItem.details.bhk.lowercase()) {
            "1 rk" -> selectCard(binding.oneRk, CardType.BHK)
            "1 bhk" -> selectCard(binding.oneBhk, CardType.BHK)
            "1.5 bhk" -> selectCard(binding.onepfiveBhk, CardType.BHK)
            "2 bhk" -> selectCard(binding.twoBhk, CardType.BHK)
            "2.5 bhk" -> selectCard(binding.twopfiveBhk, CardType.BHK)
            "3 bhk" -> selectCard(binding.threeBhk, CardType.BHK)
            "3.5 bhk" -> selectCard(binding.threepfiveBhk, CardType.BHK)
            "4 bhk" -> selectCard(binding.fourBhk, CardType.BHK)
            "4.5 bhk" -> selectCard(binding.fourpfiveBhk, CardType.BHK)
            "5 bhk" -> selectCard(binding.fiveBhk, CardType.BHK)
        }

        // Set furnish type selection
        when (roomItem.details.furnished.lowercase()) {
            "fully furnished" -> selectCard(binding.fullyFurnished, CardType.FURNISH)
            "semi furnished" -> selectCard(binding.semiFurnished, CardType.FURNISH)
            "unfurnished" -> selectCard(binding.unfurnished, CardType.FURNISH)
        }
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Update Property Details"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initOnClick() {
        binding.nextButton.setOnClickListener {
            if (validateInputs()) {
                updateRoomItemFromUI()
                navigateToPriceDetails()
            }
        }
    }

    private fun validateInputs(): Boolean {
        binding.apply {
            val building = buildingEditText.text.toString().trim()
            val locality = localityEditText.text.toString().trim()
            val area = areaEditText.text.toString().trim()

            if (building.isEmpty() || locality.isEmpty() || area.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return false
            }

            if (selectedCards[CardType.PROPERTY.key] == null) {
                Toast.makeText(requireContext(), "Please select property type", Toast.LENGTH_SHORT).show()
                return false
            }

            if (selectedCards[CardType.BHK.key] == null) {
                Toast.makeText(requireContext(), "Please select BHK type", Toast.LENGTH_SHORT).show()
                return false
            }

            if (selectedCards[CardType.FURNISH.key] == null) {
                Toast.makeText(requireContext(), "Please select furnish type", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }
    }

    private fun updateRoomItemFromUI() {
        binding.apply {
            // Create a new RoomDetails with updated values
            val updatedDetails = RoomDetails(
                type = buildingEditText.text.toString().trim(),
                location = localityEditText.text.toString().trim(),
                propertyType = getSelectedCardText(CardType.PROPERTY) ?: roomItem.details.propertyType,
                bhk = getSelectedCardText(CardType.BHK) ?: roomItem.details.bhk,
                area = areaEditText.text.toString().toIntOrNull() ?: roomItem.details.area,
                furnished = getSelectedCardText(CardType.FURNISH) ?: roomItem.details.furnished
            )

            // Create a new RoomItem with updated details
            roomItem = roomItem.copy(
                details = updatedDetails
            )
        }
    }

    private fun navigateToPriceDetails() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PriceDetailsFragment.newInstance(roomItem))
            .addToBackStack(null)
            .commit()
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
            binding.onepfiveBhk,
            binding.twoBhk,
            binding.twopfiveBhk,
            binding.threeBhk,
            binding.threepfiveBhk,
            binding.fourBhk,
            binding.fourpfiveBhk,
            binding.fiveBhk
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
                selectCard(card, type)
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
            strokeColor = resources.getColor(R.color.colorPrimary)
        }

        // Update the selected card
        selectedCards[type.key] = card
    }

    private fun getSelectedCardText(type: CardType): String? {
        return selectedCards[type.key]?.let { card ->
            findTextViewInCard(card)?.text?.toString()
        }
    }

    private fun findTextViewInCard(card: MaterialCardView): TextView? {
        // If the MaterialCardView contains only one TextView directly
        if (card.childCount == 1 && card.getChildAt(0) is TextView) {
            return card.getChildAt(0) as TextView
        }

        // Search for the first TextView recursively in the view hierarchy
        return findTextViewInViewRecursively(card)
    }

    private fun findTextViewInViewRecursively(view: View): TextView? {
        // If the view is a TextView, return it
        if (view is TextView) {
            return view
        }

        // If the view is a ViewGroup, search its children
        if (view is ViewGroup) {
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