package com.application.genzhouse.data.remote.api

import com.application.genzhouse.data.remote.model.AddRoomRequest
import com.application.genzhouse.data.remote.model.AddRoomResponse
import com.application.genzhouse.data.remote.model.AllRoomListResponse
import com.application.genzhouse.data.remote.model.DeleteRoomResponse
import com.application.genzhouse.data.remote.model.RoomListResponse
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.RoomResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RoomApiService {
    @POST("api/addRoom")
    suspend fun addRoom(
        @Header("Authorization") token: String,
        @Body request: AddRoomRequest
    ): Response<AddRoomResponse>

    @GET("api/rooms")
    suspend fun getRooms(@Header("Authorization") token: String): Response<AllRoomListResponse>

    @GET("api/userRooms")
    suspend fun getUserRooms(
        @Header("user_id") userId: Int,
        @Header("Authorization") token: String
    ): Response<RoomListResponse>

    @GET("api/ownerDashboardData")
    suspend fun getDashboardData(
        @Header("user_id") userId: Int,
        @Header("Authorization") token: String
    ): Response<RoomResponse>

    @DELETE("api/deleteRoom/{room_id}")
    suspend fun deleteRoom(
        @Path("room_id") roomId: Int,
        @Header("Authorization") token: String
    ): Response<DeleteRoomResponse>

}
