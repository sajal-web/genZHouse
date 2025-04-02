package com.application.genzhouse.ui.userhomepage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.genzhouse.R
import com.bumptech.glide.Glide
import com.application.genzhouse.databinding.ItemRoomImageBinding

class RoomImageAdapter(private val images: List<String>) :
    RecyclerView.Adapter<RoomImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(val binding: ItemRoomImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemRoomImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        Glide.with(holder.binding.imageView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_house)
            .error(R.drawable.ic_house)
            .centerCrop()
            .into(holder.binding.imageView)
    }

    override fun getItemCount(): Int = images.size
}