package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty

import com.google.gson.annotations.SerializedName

data class EditRoomRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("owner_name") val ownerName: String,
    @SerializedName("owner_mobile") val ownerMobile: String,
    @SerializedName("room_type") val roomType: String,
    @SerializedName("location") val location: String,
    @SerializedName("property_type") val propertyType: String,
    @SerializedName("society_building_project") val societyBuildingProject: String,
    @SerializedName("locality") val locality: String,
    @SerializedName("bhk") val bhk: String,
    @SerializedName("area") val area: Int,
    @SerializedName("furnished_type") val furnishedType: String,
    @SerializedName("monthly_rent") val monthlyRent: Double,
    @SerializedName("available_from") val availableFrom: String,
    @SerializedName("security_deposit") val securityDeposit: Double,
    @SerializedName("room_images") val roomImages: List<String>
)

data class EditRoomResponse(
    val statusCode: Int,
    val success: Boolean,
    val message: String,
//    val data: RoomItem
)