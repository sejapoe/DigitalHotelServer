package ru.sejapoe.application.hotel.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class Hotel(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Hotel>(Hotels)

    var name by Hotels.name
    val roomTypes by RoomType referrersOn RoomTypes.hotel
    val rooms by Room referrersOn Rooms.hotel
    val reservations by Reservation referrersOn Reservations.hotel

    fun asDTO() = HotelDTO(
        id.value,
        name,
        roomTypes.map { it.asDTO() },
        rooms.map { it.asDTO() },
        reservations.map { it.asDTO() })

    fun asLessDTO() = HotelLessDTO(id.value, name)
}

@Serializable
data class HotelDTO(
    val id: Int?,
    val name: String,
    val roomTypes: List<RoomTypeDTO>,
    val rooms: List<RoomDTO>,
    val reservations: List<ReservationDTO>
)

@Serializable
data class HotelLessDTO(
    val id: Int,
    val name: String,
)


object Hotels : IntIdTable() {
    val name = varchar("name", 128)
}