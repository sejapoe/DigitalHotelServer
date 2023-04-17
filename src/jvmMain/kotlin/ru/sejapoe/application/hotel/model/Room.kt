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

    fun asDTO() = RoomDTO(number, type.asDTO(), occupation?.asDTO())
}

@Serializable
data class RoomDTO(
    val number: Int,
    val type: RoomTypeDTO,
    val occupation: OccupationDTO?
)

object Rooms : IntIdTable() {
    val hotel = reference("hotel", Hotels)
    val number = integer("number")
    val type = reference("type", RoomTypes)
    val occupation = reference("occupation", Occupations).nullable()
}