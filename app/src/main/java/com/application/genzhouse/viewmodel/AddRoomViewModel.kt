package com.application.genzhouse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.genzhouse.data.remote.model.AddRoomRequest
import com.application.genzhouse.data.remote.model.AddRoomResponse
import com.application.genzhouse.data.repository.RoomRepository
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty.EditRoomRequest
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty.EditRoomResponse
import com.application.genzhouse.utils.Resource
import kotlinx.coroutines.launch

class AddRoomViewModel(
    private val repository: RoomRepository = RoomRepository()
) : ViewModel() {

    private val _addRoomResult = MutableLiveData<Resource<AddRoomResponse>>()
    val addRoomResult: LiveData<Resource<AddRoomResponse>> = _addRoomResult
    private val _editRoomResult = MutableLiveData<Resource<EditRoomResponse>>()
    val editRoomResult: LiveData<Resource<EditRoomResponse>> = _editRoomResult


    fun addRoom(token: String, request: AddRoomRequest) {
        viewModelScope.launch {
            _addRoomResult.value = Resource.Loading
            val result = repository.addRoom(token, request)
            _addRoomResult.value = result
        }
    }

    fun editRoom(roomId: Int, token: String, request: EditRoomRequest) {
        viewModelScope.launch {
            _editRoomResult.value = Resource.Loading
            val result = repository.editRoom(roomId,token,request)
            _editRoomResult.value = result
        }
    }
}