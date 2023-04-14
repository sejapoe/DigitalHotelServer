package ru.sejapoe.application.hotel.model

import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.sejapoe.application.db.HotelEntity
import ru.sejapoe.application.db.HotelTable

class Room(id: EntityID<Int>) : HotelEntity(id) {
    companion object : IntEntityClass<Room>(Rooms)

    override var hotel by Hotel referencedOn Rooms.hotel
    var number by Rooms.number
    var type by RoomType referencedOn Rooms.type
    var occupation by Occupation optionalReferencedOn Rooms.occupation
}

object Rooms : HotelTable() {
    val number = integer("number")
    val type = reference("type", RoomTypes)
    val occupation = optReference("occupation", Occupations)
}