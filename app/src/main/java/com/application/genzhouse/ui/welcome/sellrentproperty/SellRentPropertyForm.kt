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
import com.application.genzhouse.MyApp
import com.application.genzhouse.R
import com.application.genzhouse.databinding.ActivitySellRentPropertyBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.propertydetails.PropertyDetailsForm
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class SellRentPropertyForm : AppCompatActivity() {
    private lateinit var activitySellRentPropertyBinding: ActivitySellRentPropertyBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isLocationDialogShown = false
    // Location permission request
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                checkLocationEnabled()
            }
            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                checkLocationEnabled()
            }
            else -> {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySellRentPropertyBinding = ActivitySellRentPropertyBinding.inflate(layoutInflater)
        setContentView(activitySellRentPropertyBinding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initUI()
        checkLocationPermission()
        activitySellRentPropertyBinding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        isLocationDialogShown = false
        checkLocationPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initUI() {
        val roomBtn = activitySellRentPropertyBinding.btnRoom
        val messBtn = activitySellRentPropertyBinding.btnMess
        val pgBtn = activitySellRentPropertyBinding.btnPG
        var role = ""
        var selectedRoomType: String? = null

        activitySellRentPropertyBinding.radioRole.setOnCheckedChangeListener { group, checkedId ->
            role = when (checkedId) {
                R.id.ownerRadio -> "Owner"
                R.id.brokerRadio -> "Broker"
                else -> ""
            }
        }

        roomBtn.isSelected = true
        selectedRoomType = roomBtn.text.toString()

        val buttons = listOf(roomBtn, messBtn, pgBtn)
        buttons.forEach { button ->
            button.setOnClickListener {
                buttons.forEach { it.isSelected = false }
                button.isSelected = true
                selectedRoomType = button.text.toString()
            }
        }

        activitySellRentPropertyBinding.nextButton.setOnClickListener {
            val name = getSharedPreferences("UserProfile", MODE_PRIVATE).getString("name", null)
            val currentLocation = activitySellRentPropertyBinding.locationTextView.text.toString()

            if (role.isEmpty()) {
                Toast.makeText(this, "Please select your role", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentLocation == "......") {
                Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get fresh token before proceeding
            (application as MyApp).getCurrentToken { token ->
                if (token != null) {
                    // Proceed with the authenticated action
                    val intent = Intent(this, PropertyDetailsForm::class.java).apply {
                        putExtra("NAME", name)
                        putExtra("ROLE", role)
                        putExtra("ROOM_TYPE", selectedRoomType)
                        putExtra("LOCATION", currentLocation)
                        putExtra("AUTH_TOKEN", token)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Authentication required", Toast.LENGTH_SHORT).show()
                    // Optionally redirect to login screen
                }
            }
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
            locationPermissionRequest.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            checkLocationEnabled()
        }
    }

    private fun checkLocationEnabled() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (!isLocationDialogShown) {
                showLocationEnableDialog()
                isLocationDialogShown = true
            }
        } else {
            getLastLocation()
        }
    }

    private fun showLocationEnableDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Enable Location")
            .setMessage("Location services are disabled. Please enable location services to use this feature.")
            .setPositiveButton("Enable Location") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                Toast.makeText(this, "Location services are required", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
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
                activitySellRentPropertyBinding.locationTextView.text = cityName ?: "......"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            activitySellRentPropertyBinding.locationTextView.text = "......"
        }
    }
}