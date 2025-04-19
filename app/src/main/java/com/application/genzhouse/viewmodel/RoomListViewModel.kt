package com.application.genzhouse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.genzhouse.data.remote.model.Room
import com.application.genzhouse.data.repository.RoomRepository
import com.application.genzhouse.utils.Resource
import kotlinx.coroutines.launch

class RoomListViewModel(
    private val repository: RoomRepository = RoomRepository()
) : ViewModel() {

    private val _rooms = MutableLiveData<Resource<List<Room>>>()
    val rooms: LiveData<Resource<List<Room>>> = _rooms

    fun loadRooms(token: String) {
        viewModelScope.launch {
            _rooms.value = Resource.Loading
            val result = repository.getRooms(token)
            _rooms.value = result
        }
    }

    fun loadUserRooms(user_id: Int,token: String) {
        viewModelScope.launch {
            _rooms.value = Resource.Loading
            val result = repository.getUserRooms(user_id,token)
            _rooms.value = result
        }
    }
}