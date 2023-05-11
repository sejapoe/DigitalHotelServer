package ru.sejapoe.application.hotel

import com.google.firebase.messaging.AndroidNotification
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.*
import ru.sejapoe.application.user.Session
import ru.sejapoe.application.utils.exception
import ru.sejapoe.routing.*
import java.time.LocalDate
import java.time.LocalDateTime

@Suppress("UNUSED")
@Route("/booking")
object BookingRoute {
    @Suppress("DuplicatedCode")
    @Post("/{id}/checkIn")
    fun checkIn(id: Int, @Provided session: Session) =
        transaction {
            if (session.user.userInfo == null) throw HttpStatusCode.Forbidden.exception()
            val booking = Booking.findById(id) ?: throw HttpStatusCode.NotFound.exception()
            if (booking.guest.id != session.user.id) throw HttpStatusCode.Forbidden.exception()
            if (booking.payment == null) throw HttpStatusCode.PaymentRequired.exception()
            if (LocalDate.now() < booking.checkInDate) throw HttpStatusCode.Forbidden.exception()
            val occupation = Occupation.new {
                guest = booking.guest
                checkInDate = booking.checkInDate
                checkOutDate = booking.checkOutDate
                room = Room.find { Rooms.type eq booking.roomType.id }.firstOrNull()
                    ?: throw HttpStatusCode.Conflict.exception()
            }
            occupation.room.occupation = occupation
            booking.delete()
            occupation.id.value
        }

    // TODO: Remove on production, just for demonstration
    @Suppress("DuplicatedCode")
    @Get("/{rawId}/checkIn")
    fun checkInDemo(rawId: Int): Unit = transaction {
        val id = (rawId - 13) / 71
        if ((rawId - 13) % 71 != 0 || id <= 0) throw HttpStatusCode.Conflict.exception()
        val booking = Booking.findById(id) ?: throw HttpStatusCode.NotFound.exception()
        if (booking.payment == null) throw HttpStatusCode.PaymentRequired.exception()
        if (LocalDate.now() < booking.checkInDate) throw HttpStatusCode.Forbidden.exception()
        val occupation = Occupation.new {
            guest = booking.guest
            checkInDate = booking.checkInDate
            checkOutDate = booking.checkOutDate
            room = Room.find { Rooms.type eq booking.roomType.id }.firstOrNull()
                ?: throw HttpStatusCode.Conflict.exception()
        }
        booking.delete()
        occupation.room.occupation = occupation
        FirebaseMessaging.getInstance().sendMulticast(
            MulticastMessage.builder()
                .addAllTokens(occupation.guest.notificationTokens)
                .putData("action", "check_in")
                .putData("booking_id", id.toString())
                .build()
        )
    }

    @Post("/{id}/pay")
    fun pay(id: Int, @Provided session: Session) =
        transaction {
            if (session.user.userInfo == null) throw HttpStatusCode.Forbidden.exception()
            val booking = Booking.findById(id) ?: throw HttpStatusCode.NotFound.exception()
            if (booking.guest.id != session.user.id) throw HttpStatusCode.Forbidden.exception()
            booking.payment = Payment.new {
                this.user = session.user
                this.amount = booking.roomType.price
                this.timestamp = LocalDateTime.now()
            }
        }

    @Get("/{id}")
    fun getBooking(id: Int, @Provided session: Session) = transaction {
        if (session.user.userInfo == null) throw HttpStatusCode.Forbidden.exception()
        val booking = Booking.findById(id)?.asDTO() ?: throw HttpStatusCode.NotFound.exception()
        if (booking.guest.id != session.user.id.value) throw HttpStatusCode.Forbidden.exception()
        booking
    }

    @Delete("/{id}")
    fun deleteBooking(id: Int, @Provided session: Session) = transaction {
        if (session.user.userInfo == null) throw HttpStatusCode.Forbidden.exception()
        val booking = Booking.findById(id) ?: throw HttpStatusCode.NotFound.exception()
        if (booking.guest.id != session.user.id) throw HttpStatusCode.Forbidden.exception()
        booking.delete()
    }

    @Get("s")
    fun getBookings(@Provided session: Session) = transaction {
        if (session.user.userInfo == null) throw HttpStatusCode.Forbidden.exception()
        session.user.bookings.map(Booking::asDTO)
    }
}