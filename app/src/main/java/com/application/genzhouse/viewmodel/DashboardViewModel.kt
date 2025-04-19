package com.application.genzhouse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.genzhouse.data.remote.model.AddRoomRequest
import com.application.genzhouse.data.remote.model.AddRoomResponse
import com.application.genzhouse.data.remote.model.TotalRoomCount
import com.application.genzhouse.data.repository.RoomRepository
import com.application.genzhouse.utils.Resource
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: RoomRepository = RoomRepository()
) : ViewModel() {

    private val _dashboardResult = MutableLiveData<Resource<TotalRoomCount>>()
    val dashboardResult: LiveData<Resource<TotalRoomCount>> = _dashboardResult

    fun getDashBoardData(userId : Int,token: String,) {
        viewModelScope.launch {
            _dashboardResult.value = Resource.Loading
            val result = repository.getDashBoardData(userId, token)
            _dashboardResult.value = result
        }
    }
}