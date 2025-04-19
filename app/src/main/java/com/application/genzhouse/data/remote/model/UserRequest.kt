package com.application.genzhouse.data.remote.model

import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("name") val name: String,
    @SerializedName("phone_number") val phoneNumber: String
)

data class UserResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: UserData
)

data class UserData(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("totalRooms") val totalRooms: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)