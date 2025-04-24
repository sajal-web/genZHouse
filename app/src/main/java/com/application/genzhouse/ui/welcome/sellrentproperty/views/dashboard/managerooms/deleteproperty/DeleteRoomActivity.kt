package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.deleteproperty

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.genzhouse.MyApp
import com.application.genzhouse.databinding.ActivityDeleteRoomBinding
import com.application.genzhouse.utils.CustomProgressDialog
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.RoomItem
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.deleteproperty.adapter.RoomAdapter
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.deleteproperty.dataholder.DataHolder
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.deleteproperty.viewmodel.DeletePropertyViewModel
import com.application.genzhouse.utils.Resource

class DeleteRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteRoomBinding
    private lateinit var adapter: RoomAdapter
    private lateinit var viewModel: DeletePropertyViewModel
    private lateinit var progressDialog: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = CustomProgressDialog(this)
        setupToolbar()
        initializeRecyclerView()
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[DeletePropertyViewModel::class.java]

        viewModel.deleteRoomResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    progressDialog.show()
                }
                is Resource.Success -> {
                    DataHolder.roomItems = DataHolder.roomItems?.filter { it.id != result.data.deletedRoom.roomId }
                    initializeRecyclerView()
                    decrementTotalRooms()
                    Toast.makeText(this, result.data.message.toString(), Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Delete Rooms"
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initializeRecyclerView() {
        val roomList = DataHolder.roomItems ?: emptyList()
        adapter = RoomAdapter(roomList) { room ->
            showDeleteConfirmationDialog(room)
        }

        binding.rvDeleteProperty.apply {
            layoutManager = LinearLayoutManager(this@DeleteRoomActivity)
            adapter = this@DeleteRoomActivity.adapter
            setHasFixedSize(true)
        }
    }
    private fun decrementTotalRooms() {
        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
        val currentTotalRooms = sharedPreferences.getInt("total_rooms", 0)

        // Ensure we don't go below 0
        val newTotalRooms = if (currentTotalRooms > 0) currentTotalRooms - 1 else 0

        sharedPreferences.edit().apply {
            putInt("total_rooms", newTotalRooms)
            apply()
        }

        Toast.makeText(this, "Total rooms updated: $newTotalRooms", Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmationDialog(room : RoomItem) {
        AlertDialog.Builder(this)
            .setTitle("Delete Room")
            .setMessage("Are you sure you want to delete this room?")
            .setPositiveButton("Delete") { _, _ ->
                deleteRoom(room)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteRoom(room : RoomItem) {
        (application as MyApp).getCurrentToken { token ->
            val userId = getUserDetails().first
            if (!token.isNullOrEmpty()) {
                if (userId != null) {
                    viewModel.deleteRoom(room.id,token.toString())
                }
            } else {
                Toast.makeText(this, "Failed to retrieve token", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getUserDetails(): Pair<Int?, String?> {
        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
        val phoneNumber = sharedPreferences.getString("phoneNumber", null)
        val userId = sharedPreferences.getInt("user_id",0)
        return Pair(userId,phoneNumber)
    }
}