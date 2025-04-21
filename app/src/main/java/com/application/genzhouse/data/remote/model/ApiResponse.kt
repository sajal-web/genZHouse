package com.application.genzhouse.data.remote.model

// Add Room Request model
data class AddRoomRequest(
    val user_id: Int,
    val owner_name: String,
    val owner_mobile: String,
    val room_type: String,
    val location: String,
    val property_type: String,
    val society_building_project: String,
    val locality: String,
    val bhk: String,
    val area: Double,
    val furnished_type: String,
    val monthly_rent: Double,
    val available_from: String,
    val security_deposit: Double,
    val room_images: List<String>
)

// Response models
data class AddRoomResponse(
    val message: String,
    val data: RoomData?
)

data class DeleteRoomResponse(
    val success: Boolean,
    val message: String?,
    val deletedRoom : Room
)

data class RoomData(
    val room_id: Int,
    val user_id: Int,
    val status: String,
    val room_type: String,
    val location: String,
    val property_type: String,
    val society_building_project: String,
    val locality: String,
    val bhk: String,
    val area: Double,
    val furnished_type: String,
    val monthly_rent: Double,
    val available_from: String,
    val security_deposit: Double,
    val room_images: List<String>,
    val updatedAt: String,
    val createdAt: String
)

data class ErrorResponse(
    val success: Boolean,
    val message: String,
    val error: String? = null
)


// List all & owner room model
// 1.Owner Rooms Data Models
data class RoomListResponse(
    val success: Boolean,
    val data: OwnerRoomData
)

data class OwnerRoomData(
    val rooms: List<Room>,
    val counts: Counts
)

data class TotalRoomCount(
    val success: Boolean,
    val data : Counts
)

data class Counts(
    val totalRooms: Int,
    val bookedRooms: Int,
    val activeRooms: Int
)

// 1.All Room Data Models
data class AllRoomListResponse(
    val success: Boolean,
    val data: List<Room>
)

data class Room(
    val room_id: Int,
    val user_id: Int,
    val owner_name: String,
    val owner_mobile: String,
    val status: String,
    val room_type: String,
    val location: String,
    val property_type: String,
    val society_building_project: String,
    val locality: String,
    val bhk: String,
    val area: Double,
    val furnished_type: String,
    val monthly_rent: Int,
    val available_from: String,
    val security_deposit: Int,
    val room_images: List<String>,
    val createdAt: String,
    val updatedAt: String
)