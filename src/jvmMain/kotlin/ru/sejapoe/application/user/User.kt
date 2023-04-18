package ru.sejapoe.application.user

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import ru.sejapoe.application.hotel.model.Reservation
import ru.sejapoe.application.hotel.model.Reservations
import ru.sejapoe.application.utils.BitArray256

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var username by Users.username
    var salt by Users.salt.transform({ it.bytes }, { BitArray256(it) })
    var verifier by Users.verifier.transform({ it.toString() }, { it.toBigInteger() })
    val reservations by Reservation referrersOn Reservations.guest
}

object Users : IntIdTable() {
    val username = varchar("username", 128).uniqueIndex()
    val salt = binary("salt", 32)
    val verifier = varchar("verifier", 128)
}