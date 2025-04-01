package com.application.genzhouse.ui.userhomepage.adapter

import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.genzhouse.R
import com.application.genzhouse.data.model.Room
import com.application.genzhouse.databinding.PropertyRoomItemBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class RoomListingAdapter(
    private var rooms: List<Room> = emptyList(),
    private val onItemClick: (Room) -> Unit
) : RecyclerView.Adapter<RoomListingAdapter.RoomViewHolder>() {

    class RoomViewHolder(val binding: PropertyRoomItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = PropertyRoomItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        with(holder.binding) {
            tvRent.text = "₹${room.monthly_rent}/month"
            tvRoomType.text = room.room_type
            tvArea.text = "${room.area}sq. ft."
            tvFurnished.text = "${room.furnished_type}"
            tvLocation.text = room.location
            tvOwnerName.text = room.owner_name

            // Format available date
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

//            try {
//                val availableDate = inputFormat.parse(room.available_from)
//                val addedDate = inputFormat.parse(room.createdAt)
//                val expiringDate = inputFormat.parse(room.updatedAt)
//                tvAvailableDate.text = "Available from: ${outputFormat.format(availableDate)}"
//                tvDateAdded.text = outputFormat.format(addedDate)
//                tvDateExpiring.text = outputFormat.format(expiringDate)
//            } catch (e: Exception) {
//                tvAvailableDate.text = "Available from: ${room.available_from}"
//                tvDateAdded.text = room.createdAt
//                tvDateExpiring.text = room.updatedAt
//            }
//
//            // Load first image if available
//            if (room.room_images.isNotEmpty() && room.room_images[0].startsWith("http")) {
//                Glide.with(ivRoomImage.context)
//                    .load(room.room_images[0])
//                    .placeholder(R.drawable.ic_house)
//                    .error(R.drawable.ic_house)
//                    .centerCrop()
//                    .into(ivRoomImage)
//            } else {
//                ivRoomImage.setImageResource(R.drawable.ic_house)
//            }

            // Set click listener
            root.setOnClickListener { onItemClick(room) }
        }
    }

    override fun getItemCount() = rooms.size

    fun updateRooms(newRooms: List<Room>) {
        rooms = newRooms
        notifyDataSetChanged()
    }
}
