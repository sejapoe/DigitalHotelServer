package ru.sejapoe.application.hotel.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import ru.sejapoe.application.user.User
import ru.sejapoe.application.user.Users

class Occupation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Occupation>(Occupations)

    var guest by User referencedOn Occupations.guest
    var room by Room referencedOn Occupations.room
    var checkInDate by Occupations.checkInDate
    var checkOutDate by Occupations.checkOutDate
}

object Occupations : IntIdTable() {
    val guest = reference("guest", Users)
    val room = reference("room", Rooms)
    val checkInDate = date("check_in_date")
    val checkOutDate = date("check_out_date")
}