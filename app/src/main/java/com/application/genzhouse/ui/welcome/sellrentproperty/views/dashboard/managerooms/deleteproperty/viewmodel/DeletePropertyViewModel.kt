package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.deleteproperty.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.genzhouse.data.remote.model.DeleteRoomResponse
import com.application.genzhouse.data.repository.RoomRepository
import com.application.genzhouse.utils.Resource
import kotlinx.coroutines.launch

class DeletePropertyViewModel (
    private val repository: RoomRepository = RoomRepository()
) : ViewModel() {
    private val _deleteRoomResult = MutableLiveData<Resource<DeleteRoomResponse>>()
    val deleteRoomResult : LiveData<Resource<DeleteRoomResponse>> = _deleteRoomResult

    fun deleteRoom(roomId : Int, token: String) {
        viewModelScope.launch {
            _deleteRoomResult.value = Resource.Loading
            val result = repository.deleteRoom(roomId, token)
            _deleteRoomResult.value = result
        }
    }
}