package com.application.genzhouse.data.remote.model

import com.google.gson.annotations.SerializedName

// Add Room Request model
data class AddRoomRequest(
    @SerializedName("userId") val userId: Int,
    @SerializedName("ownerName") val ownerName: String,
    @SerializedName("ownerMobile") val ownerMobile: String,
    @SerializedName("room_type") val roomType: String,
    @SerializedName("location") val location: String,
    @SerializedName("property_type") val propertyType: String,
    @SerializedName("society_building_project") val societyBuildingProject: String,
    @SerializedName("locality") val locality: String,
    @SerializedName("bhk") val bhk: String,
    @SerializedName("area") val area: Double,
    @SerializedName("furnished_type") val furnishedType: String,
    @SerializedName("monthly_rent") val monthlyRent: Double,
    @SerializedName("available_from") val availableFrom: String,
    @SerializedName("security_deposit") val securityDeposit: Double,
    @SerializedName("room_images") val roomImages: List<String>
)

// Response models
data class AddRoomResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: RoomData?
)

data class DeleteRoomResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("deletedRoom") val deletedRoom : Room
)

data class RoomData(
    @SerializedName("room_id") val roomId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("status") val status: String,
    @SerializedName("room_type") val roomType: String,
    @SerializedName("location") val location: String,
    @SerializedName("property_type") val propertyType: String,
    @SerializedName("society_building_project") val societyBuildingProject: String,
    @SerializedName("locality") val locality: String,
    @SerializedName("bhk") val bhk: String,
    @SerializedName("area") val area: Double,
    @SerializedName("furnished_type") val furnishedType: String,
    @SerializedName("monthly_rent") val monthlyRent: Double,
    @SerializedName("available_from") val availableFrom: String,
    @SerializedName("security_deposit") val securityDeposit: Double,
    @SerializedName("room_images") val roomImages: List<String>,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("createdAt") val createdAt: String
)

data class ErrorResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("error") val error: String? = null
)


// List all & owner room model
// 1.Owner Rooms Data Models
data class RoomListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: OwnerRoomData
)

data class OwnerRoomData(
    @SerializedName("rooms") val rooms: List<Room>,
    @SerializedName("counts") val counts: Counts
)

data class Counts(
    @SerializedName("totalRooms") val totalRooms: Int,
    @SerializedName("bookedRooms") val bookedRooms: Int,
    @SerializedName("activeRooms") val activeRooms: Int
)

// 1.All Room Data Models
data class AllRoomListResponse(
    @SerializedName("room_type") val success: Boolean,
    @SerializedName("room_type") val data: List<Room>
)

data class Room(
    @SerializedName("room_id") val roomId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("owner_name") val ownerName: String,
    @SerializedName("owner_mobile") val ownerMobile: String,
    @SerializedName("status") val status: String,
    @SerializedName("room_type") val roomType: String,
    @SerializedName("location") val location: String,
    @SerializedName("property_type") val propertyType: String,
    @SerializedName("society_building_project") val societyBuildingProject: String,
    @SerializedName("locality") val locality: String,
    @SerializedName("bhk") val bhk: String,
    @SerializedName("area") val area: Double,
    @SerializedName("furnished_type") val furnishedType: String,
    @SerializedName("monthly_rent") val monthlyRent: Int,
    @SerializedName("available_from") val availableFrom: String,
    @SerializedName("security_deposit") val securityDeposit: Int,
    @SerializedName("room_images") val roomImages: List<String>,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)