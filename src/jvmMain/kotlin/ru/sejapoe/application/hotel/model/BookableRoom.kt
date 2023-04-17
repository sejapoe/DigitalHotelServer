package ru.sejapoe.application.hotel.model

import kotlinx.serialization.Serializable

@Serializable
data class BookableRoom(
    val count: Int,
    val roomType: RoomTypeDTO
)
