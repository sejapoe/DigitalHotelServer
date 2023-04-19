package ru.sejapoe.application.hotel.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import ru.sejapoe.application.user.User
import ru.sejapoe.application.user.UserDTO
import ru.sejapoe.application.user.Users
import java.time.LocalDate

class Booking(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Booking>(Bookings)

    var hotel by Hotel referencedOn Bookings.hotel
    var roomType by RoomType referencedOn Bookings.roomType
    var guest by User referencedOn Bookings.guest
    var checkInDate by Bookings.checkInDate
    var checkOutDate by Bookings.checkOutDate
    var isCancelled by Bookings.isCancelled
    var payment by Payment optionalReferencedOn Bookings.payment

    fun asDTO() = BookingDTO(
        id.value,
        hotel.asLessDTO(),
        guest.asDTO(),
        roomType.asDTO(),
        checkInDate,
        checkOutDate,
        isCancelled,
        payment?.asDTO()
    )
}

@Serializable
data class BookingDTO(
    val id: Int,
    val hotel: HotelLessDTO,
    val guest: UserDTO,
    val roomType: RoomTypeDTO,
    @Contextual val checkInDate: LocalDate,
    @Contextual val checkOutDate: LocalDate,
    val isCancelled: Boolean,
    val payment: PaymentDTO?,
)

object Bookings : IntIdTable() {
    val hotel = reference("hotel", Hotels)
    val roomType = reference("room_type", RoomTypes)
    val guest = reference("guest", Users)
    val checkInDate = date("check_in_date")
    val checkOutDate = date("check_out_date")
    val isCancelled = bool("is_cancelled").default(false)
    val payment = reference("payment", Payments).nullable()
}