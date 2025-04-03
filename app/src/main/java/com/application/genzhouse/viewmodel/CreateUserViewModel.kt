package com.application.genzhouse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.genzhouse.data.model.UserRequest
import com.application.genzhouse.data.model.UserResponse
import com.application.genzhouse.data.repository.UserRepository
import com.application.genzhouse.utils.Resource
import kotlinx.coroutines.launch

class CreateUserViewModel (
    private val repository: UserRepository = UserRepository()
) : ViewModel() {
    private  val _createUserResult = MutableLiveData<Resource<UserResponse>>()
    val createUserResult : LiveData<Resource<UserResponse>> = _createUserResult

    fun createUser(request: UserRequest) {
        viewModelScope.launch {
            _createUserResult.value = Resource.Loading
            val result = repository.createUser(request)
            _createUserResult.value = result
        }
    }
}