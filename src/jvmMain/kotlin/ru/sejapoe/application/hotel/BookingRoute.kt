package ru.sejapoe.application.hotel

import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.Booking
import ru.sejapoe.application.hotel.model.BookingDTO
import ru.sejapoe.application.hotel.model.Payment
import ru.sejapoe.application.user.Session
import ru.sejapoe.application.utils.exception
import ru.sejapoe.routing.*
import java.time.LocalDateTime

@Route("/booking")
object BookingRoute {
    @Post("/{id}/pay")
    fun pay(id: Int, @Provided session: Session) {
        transaction {
            val booking = Booking.findById(id) ?: throw HttpStatusCode.NotFound.exception()
            if (booking.guest.id != session.user.id) throw HttpStatusCode.Forbidden.exception()
            booking.payment = Payment.new {
                this.user = session.user
                this.amount = booking.roomType.price
                this.timestamp = LocalDateTime.now()
            }
        }
    }

    @Get("/{id}")
    fun getBooking(id: Int, @Provided session: Session) = transaction {
        val booking = Booking.findById(id)?.asDTO() ?: throw HttpStatusCode.NotFound.exception()
        if (booking.guest.id != session.user.id.value) throw HttpStatusCode.Forbidden.exception()
        Response(data = booking)
    }

    @Delete("/{id}")
    fun deleteBooking(id: Int, @Provided session: Session) = transaction {
        val booking = Booking.findById(id) ?: throw HttpStatusCode.NotFound.exception()
        if (booking.guest.id != session.user.id) throw HttpStatusCode.Forbidden.exception()
        booking.delete()
    }

    @Get("s")
    fun getBookings(@Provided session: Session): Response<List<BookingDTO>> {
        val bookings = transaction { session.user.bookings.map(Booking::asDTO) }
        return Response(data = bookings)
    }
}