package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.genzhouse.R
import com.application.genzhouse.databinding.FragmentUpdatePropertyBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.RoomItem
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.deleteproperty.dataholder.DataHolder
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty.adapter.EditRoomAdapter
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty.adapter.OwnerRoomsAdapter

class UpdatePropertyFragment : Fragment() {
private lateinit var binding: FragmentUpdatePropertyBinding
    private lateinit var adapter: EditRoomAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdatePropertyBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initializeRecyclerView()
        initOnClick()
    }

    private fun initOnClick() {

    }

    private fun initUI() {
        setupToolbar()
    }
    private fun initializeRecyclerView() {
        val roomList = DataHolder.ownerRooms ?: emptyList()
        adapter = EditRoomAdapter(roomList) { room ->
            showPropertyDetailsFragment(room)
        }

        binding.rvUpdateProperty.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = this@UpdatePropertyFragment.adapter
            setHasFixedSize(true)
        }
    }
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Update Property Details"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun showPropertyDetailsFragment(room : RoomItem) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PropertyDetailsFragment.newInstance(room)) // container from Activity layout
            .addToBackStack(null) // So back button returns here
            .commit()
    }
}