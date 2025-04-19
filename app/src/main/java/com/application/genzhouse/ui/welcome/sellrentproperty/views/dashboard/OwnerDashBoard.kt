package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.application.genzhouse.MyApp
import com.application.genzhouse.WelcomeActivity
import com.application.genzhouse.databinding.ActivityOwnerDashBoardBinding
import com.application.genzhouse.ui.loginregistration.CustomProgressDialog
import com.application.genzhouse.ui.welcome.sellrentproperty.views.addproperty.SellRentPropertyForm
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.ownerlistings.OwnerListing
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.ownerprofile.ProfileActivity
import com.application.genzhouse.utils.Resource
import com.application.genzhouse.viewmodel.DashboardViewModel
import com.application.genzhouse.viewmodel.RoomListViewModel

class OwnerDashBoard : AppCompatActivity() {
    private lateinit var binding: ActivityOwnerDashBoardBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var progressDialog: CustomProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = CustomProgressDialog(this)
        initOnclick()
        setupViewModel()
        getDashboardData()
    }

    private fun getUserDetails(): Pair<Int?, String?> {
        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
        val name = sharedPreferences.getString("name", null)
        val phoneNumber = sharedPreferences.getString("phoneNumber", null)
        val userId = sharedPreferences.getInt("user_id",0)
        return Pair(userId,phoneNumber)
    }

    private fun getDashboardData() {
        (application as MyApp).getCurrentToken { token ->
            val userId = getUserDetails().first
            if (!token.isNullOrEmpty()) {
                if (userId != null) {
                    viewModel.getDashBoardData(userId,token.toString())
                }
            } else {
                Toast.makeText(this, "Failed to retrieve token", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        viewModel.dashboardResult.observe(this) { result ->

            when (result) {
                is Resource.Loading -> {
                    progressDialog.show()
                }
                is Resource.Success -> {
                    binding.totalRoomsValue.text = result.data.data.totalRooms.toString()
                    binding.bookedRoomsValue.text = result.data.data.bookedRooms.toString()
                    binding.activeRoomsValue.text = result.data.data.activeRooms.toString()
                    progressDialog.dismiss()
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                }
            }
        }
    }

    private fun initOnclick() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@OwnerDashBoard, WelcomeActivity::class.java))
                finish()
            }
        })
        binding.apply {
            ivOwnerProfile.setOnClickListener {
                startActivity(Intent(this@OwnerDashBoard, ProfileActivity::class.java))
            }
            viewAllRoom.setOnClickListener {
                startActivity(Intent(this@OwnerDashBoard,OwnerListing::class.java))
            }

            ownerAddRoom.setOnClickListener {
                startActivity(Intent(this@OwnerDashBoard,SellRentPropertyForm::class.java))
            }
        }

    }
}