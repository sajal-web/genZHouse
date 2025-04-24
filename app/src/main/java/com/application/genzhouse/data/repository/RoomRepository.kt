package com.application.genzhouse.data.repository

import com.application.genzhouse.data.remote.api.RetrofitClient
import com.application.genzhouse.data.remote.model.*
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.RoomData
import com.application.genzhouse.utils.Resource
import com.application.genzhouse.utils.safeApiCall

class RoomRepository {
    private val apiService = RetrofitClient.roomApiService

    suspend fun addRoom(token: String, request: AddRoomRequest): Resource<AddRoomResponse> {
        return safeApiCall {
            apiService.addRoom("Bearer $token", request)
        }.let { result ->
            when (result) {
                is Resource.Success -> Resource.Success(result.data)
                is Resource.Error -> result
                Resource.Loading -> Resource.Loading
            }
        }
    }

    suspend fun getRooms(token: String): Resource<List<Room>> {
        return safeApiCall {
            apiService.getRooms("Bearer $token")
        }.let { result ->
            when (result) {
                is Resource.Success -> {
                    val body = result.data
                    if (body.success) {
                        Resource.Success(body.data)
                    } else {
                        Resource.Error("Unknown API error")
                    }
                }
                is Resource.Error -> result
                Resource.Loading -> Resource.Loading
            }
        }
    }

    suspend fun getUserRooms(userId: Int, token: String): Resource<List<Room>> {
        return safeApiCall {
            apiService.getUserRooms(userId, "Bearer $token")
        }.let { result ->
            when (result) {
                is Resource.Success -> {
                    val body = result.data
                    if (body.success) {
                        Resource.Success(body.data.rooms)
                    } else {
                        Resource.Error("Unknown API error")
                    }
                }
                is Resource.Error -> result
                Resource.Loading -> Resource.Loading
            }
        }
    }

    suspend fun getDashBoardData(userId: Int, token: String): Resource<RoomData> {
        return safeApiCall {
            apiService.getDashboardData(userId, "Bearer $token")
        }.let { result ->
            when (result) {
                is Resource.Success -> Resource.Success(result.data.data)
                is Resource.Error -> result
                Resource.Loading -> Resource.Loading
            }
        }
    }

    suspend fun deleteRoom(roomId: Int, token: String): Resource<DeleteRoomResponse> {
        return safeApiCall {
            apiService.deleteRoom(roomId, "Bearer $token")
        }.let { result ->
            when (result) {
                is Resource.Success -> Resource.Success(result.data)
                is Resource.Error -> result
                Resource.Loading -> Resource.Loading
            }
        }
    }
}
