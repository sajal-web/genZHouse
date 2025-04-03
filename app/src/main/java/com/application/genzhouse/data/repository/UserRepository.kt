package com.application.genzhouse.data.repository

import com.application.genzhouse.data.api.RetrofitClient
import com.application.genzhouse.data.model.ErrorResponse
import com.application.genzhouse.data.model.UserRequest
import com.application.genzhouse.data.model.UserResponse
import com.application.genzhouse.utils.Resource
import com.google.gson.Gson

class UserRepository {
    private val apiService = RetrofitClient.userApiService
    suspend fun createUser(request: UserRequest): Resource<UserResponse> {
        return try {
            val response = apiService.createUser(request)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                // Parse error response
                val errorBody = response.errorBody()?.string()
                val errorResponse = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java)
                } catch (e: Exception) {
                    ErrorResponse(false, "Unknown error occurred")
                }

                when (response.code()) {
                    401 -> Resource.Error("Unauthorized: ${errorResponse.message}")
                    422 -> Resource.Error("Validation Error: ${errorResponse.error ?: errorResponse.message}")
                    else -> Resource.Error("Error: ${errorResponse.message}")
                }
            }
        } catch (e: Exception) {
            Resource.Error("Network Error: ${e.message ?: "Unknown error occurred"}")
        }
    }
}