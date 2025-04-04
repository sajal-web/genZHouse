package com.application.genzhouse.ui.welcome.sellrentproperty.ownerdashbord

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
import com.application.genzhouse.databinding.ActivityOwnerDashBordBinding
import com.application.genzhouse.ui.loginregistration.CustomProgressDialog
import com.application.genzhouse.ui.welcome.sellrentproperty.SellRentPropertyForm
import com.application.genzhouse.ui.welcome.sellrentproperty.ownerdashbord.adapter.RoomAdapter
import com.application.genzhouse.utils.Resource
import com.application.genzhouse.viewmodel.RoomListViewModel

class OwnerDashBordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOwnerDashBordBinding
    private lateinit var viewModel: RoomListViewModel
    private lateinit var adapter: RoomAdapter
    private var backPressedOnce = false
    private lateinit var progressDialog: CustomProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerDashBordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = CustomProgressDialog(this)
        setupRecyclerView()
        setupViewModel()
        setupSwipeRefresh()
        getUserDetails()
        fetchRooms()
        initOnClick()

    }

    private fun initOnClick() {
        // Handle double back press (Toast first, then Dialog)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    showExitConfirmationDialog() // Show confirmation dialog
                } else {
                    backPressedOnce = true
                    Toast.makeText(this@OwnerDashBordActivity, "Press again to exit", Toast.LENGTH_SHORT).show()

                    // Reset `backPressedOnce` after 2 seconds
                    Handler(Looper.getMainLooper()).postDelayed({
                        backPressedOnce = false
                    }, 2000)
                }
            }
        })

        binding.apply {
            fabAddProperty.setOnClickListener {
                startActivity(Intent(this@OwnerDashBordActivity, SellRentPropertyForm::class.java))
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

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit Application")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                finishAffinity() // Clears the entire activity stack and exits the app
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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
                        binding.tvEmptyState.text = "No rooms available"
                    }
                }
                is Resource.Error -> {
                   progressDialog.dismiss()
                    binding.rvProperties.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.tvEmptyState.text = result.message
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
            adapter = this@OwnerDashBordActivity.adapter
            layoutManager = LinearLayoutManager(this@OwnerDashBordActivity)
        }
    }
}