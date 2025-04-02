package com.application.genzhouse.data.repository

import com.application.genzhouse.data.api.RetrofitClient
import com.application.genzhouse.data.model.AddRoomRequest
import com.application.genzhouse.data.model.AddRoomResponse
import com.application.genzhouse.data.model.ErrorResponse
import com.application.genzhouse.data.model.Room
import com.application.genzhouse.utils.Resource
import com.google.gson.Gson

class RoomRepository {
    private val apiService = RetrofitClient.roomApiService

    suspend fun addRoom(token: String, request: AddRoomRequest): Resource<AddRoomResponse> {
        return try {
            val response = apiService.addRoom("Bearer $token", request)
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

    suspend fun getRooms(token: String): Resource<List<Room>> {
        return try {
            val response = apiService.getRooms("Bearer $token")
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()?.data ?: emptyList())
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
                    else -> Resource.Error("Error: ${errorResponse.message}")
                }
            }
        } catch (e: Exception) {
            Resource.Error("Network Error: ${e.message ?: "Unknown error occurred"}")
        }
    }

    suspend fun getUserRooms(user_id: String,token: String): Resource<List<Room>> {
        return try {
            val response = apiService.getUserRooms(user_id,"Bearer $token")
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()?.data ?: emptyList())
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
                    else -> Resource.Error("Error: ${errorResponse.message}")
                }
            }
        } catch (e: Exception) {
            Resource.Error("Network Error: ${e.message ?: "Unknown error occurred"}")
        }
    }


}