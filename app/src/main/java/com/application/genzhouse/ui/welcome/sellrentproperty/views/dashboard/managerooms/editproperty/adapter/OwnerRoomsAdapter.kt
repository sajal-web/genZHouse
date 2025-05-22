package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.genzhouse.R
import com.application.genzhouse.databinding.DeleteRoomItemBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.RoomItem
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip

class OwnerRoomsAdapter(
    private val roomList: List<RoomItem>,
    private val onRoomClick: (RoomItem) -> Unit
) : RecyclerView.Adapter<OwnerRoomsAdapter.RoomViewHolder>() {

    inner class RoomViewHolder(private val binding: DeleteRoomItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(room: RoomItem) {
            // Load room image
            if (room.images.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(room.images[0])
                    .placeholder(R.drawable.app_logo)
                    .into(binding.ivRoomImage)
            }

            // Set basic info
            binding.tvRoomType.text = room.details.bhk
            binding.tvRent.text = room.financial.rent.toString()
            binding.tvPropertyType.text = room.details.propertyType
            binding.tvLocation.text = room.details.location
            binding.tvFurnished.text = room.details.furnished
            binding.tvArea.text = "${room.details.area} sqft"
            binding.tvDeposit.text = room.financial.deposit.toString()

            // Set status
            binding.tvActiveStatus.text = room.availability.status.uppercase()
            setStatusBackground(room.availability.status, binding.tvActiveStatus)

            // Set delete button click
            binding.roomItem.setOnClickListener {
                onRoomClick(room)
            }

        }

//        private fun formatPrice(amount: Double): String {
//            return "₹${String.format("%,d", amount)}"
//        }

        private fun setStatusBackground(status: String, chip: Chip) {
            when (status.lowercase()) {
                "rejected" -> chip.setChipBackgroundColorResource(R.color.status_rejected)
                "confirmed" -> chip.setChipBackgroundColorResource(R.color.status_confirmed)
                "booked" -> chip.setChipBackgroundColorResource(R.color.status_booked)
                else -> chip.setChipBackgroundColorResource(R.color.status_pending)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = DeleteRoomItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(roomList[position])
    }

    override fun getItemCount(): Int = roomList.size
}