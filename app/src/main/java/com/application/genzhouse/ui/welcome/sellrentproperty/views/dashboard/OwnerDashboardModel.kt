package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomResponse(
    val success: Boolean,
    val data: RoomData
) : Parcelable

@Parcelize
data class RoomData(
    val counts: RoomCounts,
    val rooms: RoomCategories
) : Parcelable

@Parcelize
data class RoomCounts(
    val totalRooms: Int,
    val bookedRooms: Int,
    val pendingRooms: Int,
    val cancelledRooms: Int,
    val activeRooms: Int
) : Parcelable

@Parcelize
data class RoomCategories(
    val booked: List<RoomItem>,
    val pending: List<RoomItem>,
    val cancelled: List<RoomItem>,
    val active: List<RoomItem>,
    val all: List<RoomItem>
) : Parcelable

@Parcelize
data class RoomItem(
    val id: Int,
    val userId: Int,
    val owner: Owner,
    val details: RoomDetails,
    val financial: RoomFinancial,
    val availability: RoomAvailability,
    val images: List<String>
) : Parcelable

@Parcelize
data class Owner(
    val name: String,
    val mobile: String
) : Parcelable

@Parcelize
data class RoomDetails(
    val type: String,
    val location: String,
    val propertyType: String,
    val bhk: String,
    val area: Int,
    val furnished: String
) : Parcelable

@Parcelize
data class RoomFinancial(
    val rent: Int,
    val deposit: Int
) : Parcelable

@Parcelize
data class RoomAvailability(
    val date: String,
    val status: String
) : Parcelable

