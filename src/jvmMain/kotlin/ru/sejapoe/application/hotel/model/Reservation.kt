package ru.sejapoe.application.hotel.model

import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.javatime.date
import ru.sejapoe.application.db.HotelEntity
import ru.sejapoe.application.db.HotelTable
import ru.sejapoe.application.user.User
import ru.sejapoe.application.user.Users

class Reservation(id: EntityID<Int>) : HotelEntity(id) {
    companion object : IntEntityClass<Reservation>(Reservations)

    override var hotel by Hotel referencedOn Reservations.hotel
    var roomType by RoomType referencedOn Reservations.roomType
    var guest by User referencedOn Reservations.guest
    var checkInDate by Reservations.checkInDate
    var checkOutDate by Reservations.checkOutDate
}

object Reservations : HotelTable() {
    val roomType = reference("room_type", RoomTypes)
    val guest = reference("guest", Users)
    val checkInDate = date("check_in_date")
    val checkOutDate = date("check_out_date")
}