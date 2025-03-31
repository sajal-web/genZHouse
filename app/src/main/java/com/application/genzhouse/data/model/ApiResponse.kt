package com.application.genzhouse.data.model

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


// List owner room model

// 1. Data Models
data class RoomListResponse(
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