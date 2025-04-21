package com.application.genzhouse.data.repository

import com.application.genzhouse.data.remote.api.RetrofitClient
import com.application.genzhouse.data.remote.model.ErrorResponse
import com.application.genzhouse.data.remote.model.UserRequest
import com.application.genzhouse.data.remote.model.UserResponse
import com.application.genzhouse.utils.Resource
import com.application.genzhouse.utils.safeApiCall
import com.google.gson.Gson

class UserRepository {
    private val apiService = RetrofitClient.userApiService

    suspend fun createUser(request: UserRequest): Resource<UserResponse> {
        return safeApiCall {
            apiService.createUser(request)
        }.let { result ->
            when (result) {
                is Resource.Success -> Resource.Success(result.data)
                is Resource.Error -> result
                Resource.Loading -> Resource.Loading
            }
        }
    }

    suspend fun checkUser(request: UserRequest): Resource<UserResponse> {
        return safeApiCall {
            apiService.checkUser(request)
        }.let { result ->
            when (result) {
                is Resource.Success -> Resource.Success(result.data)
                is Resource.Error -> result
                Resource.Loading -> Resource.Loading
            }
        }
    }

}
