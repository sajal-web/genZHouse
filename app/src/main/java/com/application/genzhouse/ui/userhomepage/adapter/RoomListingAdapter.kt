package com.application.genzhouse.ui.userhomepage.adapter
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.application.genzhouse.data.model.Room
import com.application.genzhouse.databinding.PropertyRoomItemBinding

class RoomListingAdapter(
    private var rooms: List<Room> = emptyList(),
    private val onItemClick: (Room) -> Unit
) : RecyclerView.Adapter<RoomListingAdapter.RoomViewHolder>() {

    class RoomViewHolder(val binding: PropertyRoomItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val autoScrollHandler = Handler(Looper.getMainLooper())
        private lateinit var autoScrollRunnable: Runnable
        private var currentPage = 0

        fun setupAutoScroll(viewPager: ViewPager2, imageCount: Int) {
            // Clear any existing callbacks
            autoScrollHandler.removeCallbacksAndMessages(null)

            if (imageCount <= 1) return // No need to auto-scroll if only one image

            autoScrollRunnable = object : Runnable {
                override fun run() {
                    currentPage = viewPager.currentItem
                    val nextPage = if (currentPage == imageCount - 1) 0 else currentPage + 1
                    viewPager.setCurrentItem(nextPage, true)
                    autoScrollHandler.postDelayed(this, 3000)
                }
            }
            autoScrollHandler.postDelayed(autoScrollRunnable, 3000)
        }

        fun stopAutoScroll() {
            autoScrollHandler.removeCallbacksAndMessages(null)
        }
    }

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
            // Stop any previous auto-scroll
            holder.stopAutoScroll()

            // Basic room info
            tvRent.text = "₹${room.monthly_rent}/month"
            tvRoomType.text = room.room_type
            tvArea.text = "${room.area} sq. ft."
            tvFurnished.text = room.furnished_type
            tvLocation.text = room.location
            tvOwnerName.text = room.owner_name

            // Show/hide featured badge
//            featuredBadge.visibility = if (room.isFeatured == true) View.VISIBLE else View.GONE

            // Setup ViewPager for images
            if (room.room_images.isNullOrEmpty()) {
                // If no images, show a placeholder
                val adapter = RoomImageAdapter(listOf("")) // Empty string will show placeholder
                imageViewPager.adapter = adapter
                pageIndicator.visibility = View.GONE
            } else {
                // Set up ViewPager with images
                val adapter = RoomImageAdapter(room.room_images)
                imageViewPager.adapter = adapter

                // Set up page indicator
                pageIndicator.visibility = View.VISIBLE
                pageIndicator.text = "1/${room.room_images.size}"

                // Auto-scroll every 3 seconds if more than one image
                if (room.room_images.size > 1) {
                    holder.setupAutoScroll(imageViewPager, room.room_images.size)
                }

                // Update page indicator when page changes
                imageViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        pageIndicator.text = "${position + 1}/${room.room_images.size}"
                    }
                })
            }

            // Set click listener
            root.setOnClickListener { onItemClick(room) }
        }
    }

    override fun onViewRecycled(holder: RoomViewHolder) {
        super.onViewRecycled(holder)
        // Stop auto-scroll when view is recycled
        holder.stopAutoScroll()
    }

    override fun getItemCount() = rooms.size

    fun updateRooms(newRooms: List<Room>) {
        rooms = newRooms
        notifyDataSetChanged()
    }
}