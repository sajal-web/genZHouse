package com.application.genzhouse.ui.welcome.sellrentproperty.uploadlisting

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.application.genzhouse.data.model.AddRoomRequest
import com.application.genzhouse.databinding.ActivityUploadPropertyBinding
import com.application.genzhouse.ui.loginregistration.CustomProgressDialog
import com.application.genzhouse.ui.welcome.sellrentproperty.ownerdashbord.OwnerDashBordActivity
import com.application.genzhouse.ui.welcome.sellrentproperty.uploadlisting.adapter.PhotoAdapter
import com.application.genzhouse.utils.Resource
import com.application.genzhouse.viewmodel.AddRoomViewModel
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class UploadProperty : AppCompatActivity() {
    private lateinit var photosGridRecyclerView: RecyclerView
    private lateinit var progressDialog: CustomProgressDialog
    private lateinit var viewModel: AddRoomViewModel
    private lateinit var photoAdapter: PhotoAdapter
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val photos = mutableListOf<Uri>()
    lateinit var binding: ActivityUploadPropertyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initOnClick()
        // Initialize the custom progress dialog
        progressDialog = CustomProgressDialog(this)
        viewModel = ViewModelProvider(this)[AddRoomViewModel::class.java]
        // Initialize RecyclerView
        photoAdapter = PhotoAdapter(this, photos)
        binding.photosGridRecyclerView.adapter = photoAdapter
        // Observe results
        viewModel.addRoomResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading indicator
                    progressDialog.show()
                    progressDialog.setMessage("Adding Room...")
                }

                is Resource.Success -> {
                    progressDialog.dismiss()
                    startActivity(Intent(this, OwnerDashBordActivity::class.java))
                    // Handle success
                    Toast.makeText(this, "Room added successfully!", Toast.LENGTH_SHORT).show()
                    // Navigate back or to details screen
                }

                is Resource.Error -> {
                    progressDialog.dismiss()
                    // Show error message
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun initOnClick() {
        binding.btnAddRoom.setOnClickListener {
            progressDialog.show()
            progressDialog.setMessage("Uploading Images...")
            val token = getStoredFirebaseToken().toString()

            if (photos.isNotEmpty()) {
                uploadImagesToFirebase(photos) { uploadedImageUrls ->
                    val updatedRequest = createAddRoomRequestFromUI(uploadedImageUrls)
                    viewModel.addRoom(token, updatedRequest) // Update API with images
                }
            }
        }
    }

    fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple selections
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

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

    private fun createAddRoomRequestFromUI(imageUrls: List<String>): AddRoomRequest {
        val name = getSharedPreferences("UserProfile", MODE_PRIVATE).getString("name", null) ?: ""
        val number = getSharedPreferences("UserProfile", MODE_PRIVATE).getString("phoneNumber", null) ?: ""
        val roomType = intent.getStringExtra("ROOM_TYPE") ?: ""
        val location = intent.getStringExtra("LOCATION") ?: ""
        val building = intent.getStringExtra("BUILDING") ?: ""
        val locality = intent.getStringExtra("LOCALITY") ?: ""
        val area = intent.getDoubleExtra("AREA", 0.0)
        val furnishedType = intent.getStringExtra("FURNISHED_TYPE") ?: ""
        val bhk = intent.getStringExtra("BHK") ?: ""
        val propertyType = intent.getStringExtra("PROPERTY_TYPE") ?: ""
        val availableFrom = intent.getStringExtra("AVAILABLE_FROM") ?: ""
        val monthlyRent = intent.getDoubleExtra("MONTHLY_RENT", 0.0)
        val securityDeposit = intent.getDoubleExtra("SECURITY_DEPOSIT", 0.0)

        return AddRoomRequest(
            user_id = getSharedPreferences("UserProfile", MODE_PRIVATE).getInt("user_id",0),
            room_type = roomType,
            owner_name = name,
            owner_mobile = number,
            location = "$building, $locality, $location",
            property_type = propertyType,
            society_building_project = building,
            locality = locality,
            bhk = bhk,
            area = area,
            furnished_type = furnishedType,
            monthly_rent = monthlyRent,
            available_from = availableFrom,
            security_deposit = securityDeposit,
            room_images = imageUrls // Store Firebase URLs instead of local URIs
        )
    }


    fun getStoredFirebaseToken(): String? {
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("firebase_token", null)
    }

    private fun uploadImagesToFirebase(images: List<Uri>, callback: (List<String>) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("room_images")
        val uploadedImageUrls = mutableListOf<String>()

        coroutineScope.launch(Dispatchers.IO) {  // Run in background
            for (imageUri in images) {
                val imageRef = storageRef.child("${System.currentTimeMillis()}.jpg")

                try {
                    val uploadTask = imageRef.putFile(imageUri).await()
                    val uri = imageRef.downloadUrl.await()

                    withContext(Dispatchers.Main) {  // Switch back to Main Thread
                        uploadedImageUrls.add(uri.toString())
                        if (uploadedImageUrls.size == images.size) {
                            callback(uploadedImageUrls)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@UploadProperty, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    fun compressImage(context: Context, imageUri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // 70% quality compression

        return outputStream.toByteArray()
    }


}