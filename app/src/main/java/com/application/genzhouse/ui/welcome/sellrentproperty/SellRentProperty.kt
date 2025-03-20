package com.application.genzhouse.ui.welcome.sellrentproperty

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.application.genzhouse.databinding.ActivitySellRentPropertyBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.propertydetails.PropertyDetails
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class SellRentProperty : AppCompatActivity() {
    private lateinit var activitySellRentPropertyBinding: ActivitySellRentPropertyBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isLocationDialogShown = false // Flag to track if the dialog has been shown

    // Register the permissions callback
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted
                checkLocationEnabled()
            }
            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Approximate location access granted
                checkLocationEnabled()
            }
            else -> {
                // No location access granted
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySellRentPropertyBinding = ActivitySellRentPropertyBinding.inflate(layoutInflater)
        setContentView(activitySellRentPropertyBinding.root)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initUI()
        checkLocationPermission()
    }

    override fun onResume() {
        super.onResume()
        // Reset the dialog flag when the app resumes
        isLocationDialogShown = false
        // Recheck location status and fetch location name when the app resumes
        checkLocationPermission()
    }

    private fun initUI() {
        val rentBtn = activitySellRentPropertyBinding.rentButton
        val sellBtn = activitySellRentPropertyBinding.sellButton
        val pgBtn = activitySellRentPropertyBinding.pgButton
        rentBtn.isSelected = true
        val buttons = listOf(rentBtn, sellBtn, pgBtn)

        buttons.forEach { button ->
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

        activitySellRentPropertyBinding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        activitySellRentPropertyBinding.nextButton.setOnClickListener {
            startActivity(Intent(this, PropertyDetails::class.java))
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions
            locationPermissionRequest.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            // Permission already granted, check if location is enabled
            checkLocationEnabled()
        }
    }

    private fun checkLocationEnabled() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Location is not enabled, show an AlertDialog to prompt the user (only if not already shown)
            if (!isLocationDialogShown) {
                showLocationEnableDialog()
                isLocationDialogShown = true // Mark the dialog as shown
            }
        } else {
            // Location is enabled, get the last known location
            getLastLocation()
        }
    }

    private fun showLocationEnableDialog() {
        val alertDialog = android.app.AlertDialog.Builder(this)
            .setTitle("Enable Location")
            .setMessage("Location services are disabled. Please enable location services to use this feature.")
            .setPositiveButton("Enable Location") { dialog, which ->
                // Redirect user to location settings
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Handle cancel action (optional)
                Toast.makeText(this, "Location services are required", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false) // Prevent dismissing the dialog by tapping outside
            .create()

        alertDialog.show()
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Use Geocoder to get the city name from latitude and longitude
                getCityName(location.latitude, location.longitude)
            } else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCityName(latitude: Double, longitude: Double) {
        val geocoder = android.location.Geocoder(this, java.util.Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val cityName = addresses[0].locality
                if (cityName != null) {
                    // Update the TextView with the city name
                    activitySellRentPropertyBinding.locationTextView.text = cityName
                } else {
                    activitySellRentPropertyBinding.locationTextView.text = "......" // Default value
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            activitySellRentPropertyBinding.locationTextView.text = "......" // Default value
        }
    }
}