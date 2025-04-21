package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.ownerlistings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.genzhouse.MyApp
import com.application.genzhouse.databinding.ActivityOwnerListingBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.OwnerDashBoard
import com.application.genzhouse.ui.loginregistration.CustomProgressDialog
import com.application.genzhouse.ui.welcome.sellrentproperty.views.addproperty.SellRentPropertyForm
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.ownerlistings.adapter.RoomAdapter
import com.application.genzhouse.utils.Resource
import com.application.genzhouse.viewmodel.RoomListViewModel

class OwnerListing : AppCompatActivity() {
    private lateinit var binding: ActivityOwnerListingBinding
    private lateinit var viewModel: RoomListViewModel
    private lateinit var adapter: RoomAdapter
    private lateinit var progressDialog: CustomProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerListingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = CustomProgressDialog(this)
        initToolbar()
        setupRecyclerView()
        setupViewModel()
        setupSwipeRefresh()
        getUserDetails()
        fetchRooms()
        initOnClick()

    }

    private fun initOnClick() {

        binding.apply {
            fabAddProperty.setOnClickListener {
                startActivity(Intent(this@OwnerListing, SellRentPropertyForm::class.java))
            }
        }

    }

    private fun getUserDetails(): Pair<Int?, String?> {
        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
        val name = sharedPreferences.getString("name", null)
        val phoneNumber = sharedPreferences.getString("phoneNumber", null)
        val userId = sharedPreferences.getInt("user_id",0)
        return Pair(userId,phoneNumber)
    }


    private fun fetchRooms() {
        (application as MyApp).getCurrentToken { token ->
            val userId = getUserDetails().first
            if (!token.isNullOrEmpty()) {
                if (userId != null) {
                    viewModel.loadUserRooms(userId,token.toString())
                }
            } else {
                Toast.makeText(this, "Failed to retrieve token", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            (application as MyApp).getCurrentToken { token ->
                getUserDetails().first?.let { viewModel.loadUserRooms(it,token.toString()) }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[RoomListViewModel::class.java]

        viewModel.rooms.observe(this) { result ->
            binding.swipeRefreshLayout.isRefreshing = false

            when (result) {
                is Resource.Loading -> {
                    progressDialog.show()
                    binding.tvEmptyState.visibility = View.GONE
                }
                is Resource.Success -> {
                   progressDialog.dismiss()
                    binding.tvEmptyState.visibility = View.GONE

                    if (result.data.isNotEmpty()) {
                        adapter.updateRooms(result.data)
                    } else {
                        binding.tvEmptyState.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.tvEmptyState.text = "No rooms available!"
                    }
                }
                is Resource.Error -> {
                   progressDialog.dismiss()
                    binding.rvProperties.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.tvEmptyState.text = result.message
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = RoomAdapter { room ->
            Toast.makeText(this,"${room.room_type} clicked!", Toast.LENGTH_LONG).show()
            // Handle room item click
//            val intent = Intent(this, RoomDetails::class.java).apply {
//                putExtra("ROOM_ID", room.room_id)
//            }
//            startActivity(intent)
        }

        binding.rvProperties.apply {
            adapter = this@OwnerListing.adapter
            layoutManager = LinearLayoutManager(this@OwnerListing)
        }
    }
    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}