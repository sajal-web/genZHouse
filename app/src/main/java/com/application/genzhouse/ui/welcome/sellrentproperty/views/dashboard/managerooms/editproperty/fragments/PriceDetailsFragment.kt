package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.application.genzhouse.R
import com.application.genzhouse.databinding.FragmentPriceDetailsBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.RoomItem
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class PriceDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPriceDetailsBinding
    private lateinit var roomItem: RoomItem

    companion object {
        private const val ARG_ROOM_ITEM = "room_item"

        fun newInstance(room: RoomItem): PriceDetailsFragment {
            val fragment = PriceDetailsFragment()
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
            roomItem = it.getParcelable(ARG_ROOM_ITEM) ?: throw IllegalStateException("RoomItem must not be null")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPriceDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initClickListeners()
        displayData()
    }

    private fun initUI() {
        setupToolbar()
        binding.btnUpdateProperty.text = "Next"
    }

    private fun displayData() {
        // Your original date string
        val isoDateString = roomItem.availability.date

// Parse the ISO string to a Date object
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        isoFormat.timeZone = TimeZone.getTimeZone("UTC") // Important for Zulu time (UTC)
        val date = isoFormat.parse(isoDateString)

// Format the Date object to your desired format
        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = displayFormat.format(date)
        binding.apply {
            // Set initial values

            monthlyRentEditText.setText(if (roomItem.financial.rent % 1 == 0.0) "%.0f".format(roomItem.financial.rent) else "${roomItem.financial.rent}")
            availableFromEditText.setText(formattedDate)

            // Set deposit selection
            when (roomItem.financial.deposit) {
                0.0 -> selectDepositCard(noneDepositCard)
                roomItem.financial.rent -> selectDepositCard(oneMonthDepositCard)
                roomItem.financial.rent * 2 -> selectDepositCard(towMonthDepositCard)
                else -> {
                    selectDepositCard(customDepositCard)
                    customAmountEdittext.visibility = View.VISIBLE
                    customRent.setText(if(roomItem.financial.deposit % 1 == 0.0) "%.0f".format(roomItem.financial.rent) else "${roomItem.financial.deposit}" )
                }
            }
        }
    }

    private fun initClickListeners() {
        binding.apply {
            // Deposit selection
            noneDepositCard.setOnClickListener { selectDepositCard(it as MaterialCardView) }
            oneMonthDepositCard.setOnClickListener { selectDepositCard(it as MaterialCardView) }
            towMonthDepositCard.setOnClickListener { selectDepositCard(it as MaterialCardView) }
            customDepositCard.setOnClickListener {
                selectDepositCard(it as MaterialCardView)
                customAmountEdittext.visibility = View.VISIBLE
            }

            // Date picker
            availableFromEditText.setOnClickListener { showDatePicker() }

            // Next button
            btnUpdateProperty.setOnClickListener {
                if (validateInputs()) {
                    updateRoomItem()
                    navigateToUploadPhotos()
                }
            }
        }
    }

    private fun selectDepositCard(card: MaterialCardView) {
        // Reset all cards
        listOf(
            binding.noneDepositCard,
            binding.oneMonthDepositCard,
            binding.towMonthDepositCard,
            binding.customDepositCard
        ).forEach { it.strokeWidth = 0 }

        // Select clicked card
        card.strokeWidth = 2
        card.strokeColor = resources.getColor(R.color.colorPrimary)

        // Hide custom amount if not needed
        if (card != binding.customDepositCard) {
            binding.customAmountEdittext.visibility = View.GONE
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = "${day}/${month + 1}/${year}"
                binding.availableFromEditText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun validateInputs(): Boolean {
        binding.apply {
            // Validate rent
            val rent = monthlyRentEditText.text.toString().toIntOrNull()
            if (rent == null || rent <= 0) {
                showError("Please enter valid rent amount")
                return false
            }

            // Validate date
            if (availableFromEditText.text.isNullOrEmpty()) {
                showError("Please select available date")
                return false
            }

            // Validate custom deposit
            if (binding.customDepositCard.strokeWidth > 0 &&
                binding.customRent.text.toString().toIntOrNull() == null) {
                showError("Please enter valid deposit amount")
                return false
            }

            return true
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun updateRoomItem() {
        binding.apply {
            // Safely parse rent as Double with error handling
            val rent = try {
                monthlyRentEditText.text.toString().toDouble()
            } catch (e: NumberFormatException) {
                // Show error and return if parsing fails
                monthlyRentEditText.error = "Please enter a valid amount"
                return
            }

            // Calculate deposit based on selection
            val deposit = when {
                noneDepositCard.strokeWidth > 0 -> 0.0
                oneMonthDepositCard.strokeWidth > 0 -> rent
                towMonthDepositCard.strokeWidth > 0 -> rent * 2
                else -> {
                    // Safely parse custom deposit
                    try {
                        customRent.text.toString().toDouble()
                    } catch (e: NumberFormatException) {
                        customRent.error = "Please enter a valid amount"
                        return
                    }
                }
            }

            // Update roomItem with Double values
            // Get date from EditText (e.g., "30/04/2025")
            val dateStr = availableFromEditText.text.toString()

            // Parse the DD/MM/YYYY format
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateStr)

            // Convert to ISO 8601 format (UTC/Zulu time)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("UTC") // Ensure UTC timezone
            val isoDateString = outputFormat.format(date)
            roomItem = roomItem.copy(
                financial = roomItem.financial.copy(
                    rent = rent,
                    deposit = deposit
                ),
                availability = roomItem.availability.copy(
                    date = isoDateString
                )
            )
        }
    }

    private fun navigateToUploadPhotos() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, UploadPhotoFragment.newInstance(roomItem))
            .addToBackStack(null)
            .commit()
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Update Price Details"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}