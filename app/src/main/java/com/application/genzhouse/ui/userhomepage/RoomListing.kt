package com.application.genzhouse.ui.userhomepage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.genzhouse.MyApp
import com.application.genzhouse.databinding.ActivityRoomListingBinding
import com.application.genzhouse.ui.loginregistration.CustomProgressDialog
import com.application.genzhouse.ui.userhomepage.adapter.RoomListingAdapter
import com.application.genzhouse.utils.Resource
import com.application.genzhouse.viewmodel.RoomListViewModel

class RoomListing : AppCompatActivity() {
    private lateinit var viewModel: RoomListViewModel
    private lateinit var adapter: RoomListingAdapter
    private lateinit var progressDialog: CustomProgressDialog
    private lateinit var binding: ActivityRoomListingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomListingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize the custom progress dialog
        progressDialog = CustomProgressDialog(this)
        setupRecyclerView()
        setupViewModel()
        setupSwipeRefresh()

        fetchRooms()

    }
    private fun setupRecyclerView() {
        adapter = RoomListingAdapter { room ->
            Toast.makeText(this,"${room.room_type} clicked!", Toast.LENGTH_LONG).show()
            // Handle room item click
            val intent = Intent(this, RoomDetails::class.java).apply {
                putExtra("ROOM_ID", room.room_id)
            }
            startActivity(intent)
        }

        binding.propertyRecyclerView.apply {
            adapter = this@RoomListing.adapter
            layoutManager = LinearLayoutManager(this@RoomListing)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[RoomListViewModel::class.java]

        viewModel.rooms.observe(this) { result ->
            binding.swipeRefreshLayout.isRefreshing = false

            when (result) {
                is Resource.Loading -> {
                    progressDialog.show()
                    binding.emptyStateView.visibility = View.GONE
                }
                is Resource.Success -> {
                    progressDialog.dismiss()
                    binding.emptyStateView.visibility = View.GONE

                    if (result.data.isNotEmpty()) {
                        adapter.updateRooms(result.data)
                    } else {
                        binding.emptyStateView.visibility = View.VISIBLE
                        binding.emptyStateTitle.text = "No rooms available"
                    }
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    binding.propertyRecyclerView.visibility = View.GONE
                    binding.emptyStateView.visibility = View.VISIBLE
                    binding.emptyStateSubtitle.text = result.message
                }
            }
        }
    }
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            (application as MyApp).getCurrentToken { token ->
                viewModel.loadRooms(token.toString())
            }
        }
    }

    private fun fetchRooms() {
        (application as MyApp).getCurrentToken { token ->
            if (!token.isNullOrEmpty()) {
                viewModel.loadRooms(token)
            } else {
                Toast.makeText(this, "Failed to retrieve token", Toast.LENGTH_SHORT).show()
            }
        }
    }
}