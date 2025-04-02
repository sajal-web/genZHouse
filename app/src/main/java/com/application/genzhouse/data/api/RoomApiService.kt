package com.application.genzhouse.data.api

import com.application.genzhouse.data.model.AddRoomRequest
import com.application.genzhouse.data.model.AddRoomResponse
import com.application.genzhouse.data.model.RoomListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface RoomApiService {
    @POST("api/add-room")
    suspend fun addRoom(
        @Header("Authorization") token: String,
        @Body request: AddRoomRequest
    ): Response<AddRoomResponse>

    @GET("api/rooms")
    suspend fun getRooms(@Header("Authorization") token: String): Response<RoomListResponse>

    @GET("api/user-rooms")
    suspend fun getUserRooms(
        @Header("user_id") userId: String,
        @Header("Authorization") token: String
    ): Response<RoomListResponse>



}
