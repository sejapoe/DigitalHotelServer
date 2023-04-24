package ru.sejapoe.application.hotel.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class Room(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Room>(Rooms)

    var hotel by Hotel referencedOn Rooms.hotel
    var number by Rooms.number
    var type by RoomType referencedOn Rooms.type
    var occupation by Occupation optionalReferencedOn Rooms.occupation
    var isOpen by Rooms.isOpen

    fun asDTO() = RoomDTO(number, type.asDTO(), occupation?.asDTO())
    fun asLessDTO() = RoomLessDTO(id.value, hotel.asLessDTO(), number, type.asDTO(), isOpen)
}

@Serializable
data class RoomDTO(
    val number: Int,
    val type: RoomTypeDTO,
    val occupation: OccupationDTO?
)

@Serializable
data class RoomLessDTO(
    val id: Int,
    val hotel: HotelLessDTO,
    val number: Int,
    val type: RoomTypeDTO,
    val isOpen: Boolean,
)

object Rooms : IntIdTable() {
    val hotel = reference("hotel", Hotels)
    val number = integer("number")
    val type = reference("type", RoomTypes)
    val occupation = reference("occupation", Occupations).nullable()
    val isOpen = bool("is_open").default(false)
}