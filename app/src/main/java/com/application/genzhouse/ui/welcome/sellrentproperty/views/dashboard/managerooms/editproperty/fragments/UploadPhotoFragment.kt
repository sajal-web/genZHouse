package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.genzhouse.MyApp
import com.application.genzhouse.databinding.FragmentUploadPhoto2Binding
import com.application.genzhouse.ui.welcome.sellrentproperty.views.addproperty.uploadphotos.adapter.OnAddPhotoClickListener
import com.application.genzhouse.ui.welcome.sellrentproperty.views.addproperty.uploadphotos.adapter.PhotoAdapter
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.OwnerDashBoard
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.RoomItem
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty.EditRoomRequest
import com.application.genzhouse.utils.CustomProgressDialog
import com.application.genzhouse.utils.Resource
import com.application.genzhouse.viewmodel.AddRoomViewModel

class UploadPhotoFragment : Fragment(), OnAddPhotoClickListener {

    private lateinit var binding: FragmentUploadPhoto2Binding
    private lateinit var roomItem: RoomItem
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var viewModel: AddRoomViewModel
    private lateinit var progressDialog: CustomProgressDialog

    private val selectedPhotos = mutableListOf<Uri>()
    private val alreadyUploadedPhotos = mutableListOf<String>()
    private val newPhotosToUpload = mutableListOf<Uri>()

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (!selectedPhotos.contains(it)) {
                selectedPhotos.add(it)
                newPhotosToUpload.add(it)
                photoAdapter.notifyItemInserted(selectedPhotos.size - 1)
            }
        }
    }

    companion object {
        private const val ARG_ROOM_ITEM = "room_item"
        private const val MAX_PHOTOS = 8

        fun newInstance(room: RoomItem): UploadPhotoFragment {
            val fragment = UploadPhotoFragment()
            val args = Bundle().apply {
                putParcelable(ARG_ROOM_ITEM, room)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            roomItem = it.getParcelable(ARG_ROOM_ITEM)
                ?: throw IllegalStateException("RoomItem must not be null")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadPhoto2Binding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AddRoomViewModel::class.java]
        initUI()
        initViewModel()
        initClickListeners()
        setupPhotoRecyclerView()
    }

    private fun initUI() {
        setupToolbar()
        progressDialog = CustomProgressDialog(requireContext())
        binding.btnAddRoom.text = "Update Room"
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Update Photos"
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
    override fun onAddPhotoClicked() {
        openImagePicker()
    }
    private fun setupPhotoRecyclerView() {
        photoAdapter = PhotoAdapter(requireContext(), selectedPhotos, this)
        binding.photosGridRecyclerView.adapter = photoAdapter


        // Load already uploaded images
        roomItem.images.forEach { url ->
            val uri = Uri.parse(url)
            selectedPhotos.add(uri)
            alreadyUploadedPhotos.add(url)
        }
        photoAdapter.notifyDataSetChanged()
    }

    private fun initClickListeners() {
        binding.btnAddRoom.setOnClickListener {
            if (validatePhotos()) {
                updateRoomWithPhotos()
            }
        }
    }

    private fun validatePhotos(): Boolean {
        return if (selectedPhotos.count() >= 3) {
            true
        } else {
            Toast.makeText(requireContext(), "Please add at least 3 photo", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun initViewModel() {
        viewModel.editRoomResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    progressDialog.show()
                    progressDialog.setMessage("Updating Room...")
                }

                is Resource.Success -> {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Room updated successfully!", Toast.LENGTH_SHORT).show()
                    navigateToDashboard()
                }

                is Resource.Error -> {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun updateRoomWithPhotos() {
        val imageUrls = alreadyUploadedPhotos.toMutableList()

        // In actual upload scenario, upload newPhotosToUpload and get URLs first
        newPhotosToUpload.forEach { uri ->
            imageUrls.add(uri.toString()) // Replace with uploaded image URLs if needed
        }

        roomItem = roomItem.copy(images = imageUrls)

        val request = EditRoomRequest(
            userId = roomItem.userId,
            ownerName = roomItem.owner.name,
            ownerMobile = roomItem.owner.mobile,
            roomType = roomItem.details.type,
            location = roomItem.details.location,
            propertyType = roomItem.details.propertyType,
            societyBuildingProject = roomItem.details.propertyType,
            locality = roomItem.details.location,
            bhk = roomItem.details.bhk,
            area = roomItem.details.area,
            furnishedType = roomItem.details.furnished,
            monthlyRent = roomItem.financial.rent,
            availableFrom = roomItem.availability.date,
            securityDeposit = roomItem.financial.deposit,
            roomImages = imageUrls
        )

        getCurrentToken { token ->
            if (!token.isNullOrEmpty()) {
                viewModel.editRoom(roomItem.id, token, request)
            } else {
                Toast.makeText(requireContext(), "Failed to retrieve token", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentToken(callback: (String?) -> Unit) {
        (requireActivity().application as? MyApp)?.getCurrentToken { token ->
            callback(token)
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(requireContext(), OwnerDashBoard::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    fun openImagePicker() {
        if (selectedPhotos.size >= MAX_PHOTOS) {
            Toast.makeText(
                requireContext(),
                "You can upload maximum $MAX_PHOTOS photos",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        imagePickerLauncher.launch("image/*")
    }
}
