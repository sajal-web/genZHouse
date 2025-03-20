package com.application.genzhouse.ui.welcome.sellrentproperty.uploadlisting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.application.genzhouse.R
import com.application.genzhouse.databinding.ActivityUploadPropertyBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.uploadlisting.adapter.PhotoAdapter

class UploadProperty : AppCompatActivity() {
    private lateinit var photosGridRecyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private val photos = mutableListOf<Uri>()
    lateinit var binding: ActivityUploadPropertyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPropertyBinding.inflate(layoutInflater)
        setContentView(R.layout.fragment_upload_photo)
        binding.btnUploadFromGallery.setOnClickListener{
            openImagePicker()
        }
        // Initialize RecyclerView
        photosGridRecyclerView = findViewById(R.id.photosGridRecyclerView)
        photoAdapter = PhotoAdapter(this, photos)
        photosGridRecyclerView.adapter = photoAdapter
    }
    fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple selections
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.clipData != null) {
                // Multiple images selected
                val clipData = data.clipData
                for (i in 0 until clipData!!.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    photoAdapter.addPhoto(imageUri)
                }
            } else if (data.data != null) {
                // Single image selected
                val imageUri = data.data
                if (imageUri != null) {
                    photoAdapter.addPhoto(imageUri)
                }
            }
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 100
    }
}