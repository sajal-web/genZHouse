package com.application.genzhouse.ui.welcome.sellrentproperty.views.addproperty.uploadphotos.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.application.genzhouse.R
import com.application.genzhouse.ui.welcome.sellrentproperty.views.addproperty.uploadphotos.UploadProperty

class PhotoAdapter(
    private val context: Context,
    private val photos: MutableList<Uri>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ADD_MORE = 0
        private const val VIEW_TYPE_PHOTO = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ADD_MORE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_add_more, parent, false)
                AddMoreViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
                PhotoViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddMoreViewHolder -> {
                // Handle "Add More" button click
                holder.itemView.setOnClickListener {
                    // Open gallery or camera to add a photo
                    (context as? UploadProperty)?.openImagePicker()
                }
            }
            is PhotoViewHolder -> {
                val photoUri = photos[position - 1] // Adjust position for "Add More" button
                holder.bind(photoUri)
            }
        }
    }

    override fun getItemCount(): Int = photos.size + 1 // +1 for the "Add More" button

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_ADD_MORE else VIEW_TYPE_PHOTO
    }

    fun addPhoto(uri: Uri) {
        if (photos.contains(uri)) {
            Toast.makeText(context, "This image is already added.", Toast.LENGTH_SHORT).show()
        } else {
            photos.add(uri)
            notifyItemInserted(photos.size) // Notify adapter of new item
        }
    }

    inner class AddMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.photoImageView)
        private val removeButton: ImageButton = itemView.findViewById(R.id.removeButton)

        fun bind(uri: Uri) {
            Glide.with(context).load(uri).into(imageView)
            removeButton.setOnClickListener {
                photos.removeAt(adapterPosition - 1) // Adjust position for "Add More" button
                notifyItemRemoved(adapterPosition)
            }
        }
    }
}