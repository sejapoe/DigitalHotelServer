package ru.sejapoe.application.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import ru.sejapoe.application.hotel.model.Hotel
import ru.sejapoe.application.hotel.model.Hotels

abstract class HotelEntity(id: EntityID<Int>) : IntEntity(id) {
    abstract var hotel: Hotel;
}

open class HotelTable : IntIdTable() {
    val hotel = reference("hotel", Hotels)
}