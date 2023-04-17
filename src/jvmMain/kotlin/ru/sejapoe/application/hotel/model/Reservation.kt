package ru.sejapoe.application.hotel.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import ru.sejapoe.application.user.User
import ru.sejapoe.application.user.Users
import java.time.LocalDate

class Reservation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Reservation>(Reservations)

    var hotel by Hotel referencedOn Reservations.hotel
    var roomType by RoomType referencedOn Reservations.roomType
    var guest by User referencedOn Reservations.guest
    var checkInDate by Reservations.checkInDate
    var checkOutDate by Reservations.checkOutDate

    fun asDTO() = ReservationDTO(roomType.asDTO(), checkInDate, checkOutDate)
}

@Serializable
data class ReservationDTO(
    val roomType: RoomTypeDTO,
    @Contextual val checkInDate: LocalDate,
    @Contextual val checkOutDate: LocalDate,
)

object Reservations : IntIdTable() {
    val hotel = reference("hotel", Hotels)
    val roomType = reference("room_type", RoomTypes)
    val guest = reference("guest", Users)
    val checkInDate = date("check_in_date")
    val checkOutDate = date("check_out_date")
}