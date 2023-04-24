package ru.sejapoe.application.hotel.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import ru.sejapoe.application.user.User
import ru.sejapoe.application.user.UserDTO
import ru.sejapoe.application.user.Users
import java.time.ZoneOffset

class Payment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Payment>(Payments)

    var user by User referencedOn Payments.user
    var amount by Payments.amount
    var timestamp by Payments.timestamp

    fun asDTO() = PaymentDTO(id.value, user.asDTO(), amount, timestamp.toEpochSecond(ZoneOffset.UTC))
}

@Serializable
data class PaymentDTO(val id: Int, val user: UserDTO, val amount: Int, val timestamp: Long)

object Payments : IntIdTable() {
    val user = reference("payer", Users)
    val amount = integer("amount")
    val timestamp = datetime("timestamp")
}