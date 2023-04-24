package ru.sejapoe.application.hotel

import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.*
import ru.sejapoe.application.user.Session
import ru.sejapoe.application.utils.exception
import ru.sejapoe.routing.Get
import ru.sejapoe.routing.Post
import ru.sejapoe.routing.Provided
import ru.sejapoe.routing.Route
import java.time.LocalDate
import kotlin.math.max

@Route("/book/{hotelId}/{checkIn}/{checkOut}")
object BookRoute {
    @Get
    fun getBookableRooms(
        hotelId: Int,
        checkIn: LocalDate,
        checkOut: LocalDate,
        @Provided session: Session
    ): List<BookableRoom> = transaction {
        if (session.user.userInfo == null) throw HttpStatusCode.Forbidden.exception()
        val hotel = Hotel.findById(hotelId)?.asDTO() ?: throw HttpStatusCode.NotFound.exception()
        val bookableRooms =
            getBookableRooms(
                hotel,
                checkIn,
                checkOut,
                session.user.id.value
            ).map { (type, count) -> BookableRoom(count, type) }
        if (bookableRooms.isEmpty()) throw HttpStatusCode.NotFound.exception()
        bookableRooms
    }


    @Post("/{roomTypeId}")
    fun book(
        hotelId: Int,
        checkIn: LocalDate,
        checkOut: LocalDate,
        roomTypeId: Int,
        @Provided session: Session
    ) =
        transaction {
            if (session.user.userInfo == null) throw HttpStatusCode.Forbidden.exception()
            val hotel = Hotel.findById(hotelId)?.asDTO() ?: throw HttpStatusCode.NotFound.exception()
            val bookableRooms =
                getBookableRooms(hotel, checkIn, checkOut, session.user.id.value)
            val count =
                bookableRooms.mapKeys { it.key.id }[roomTypeId] ?: throw HttpStatusCode.NotFound.exception()
            if (count <= 0) throw HttpStatusCode.NotFound.exception()
            Booking.new {
                this.hotel = Hotel[hotelId]
                this.checkInDate = checkIn
                this.checkOutDate = checkOut
                roomType = RoomType[roomTypeId]
                guest = session.user
            }
        }

    private fun getBookableRooms(
        hotel: HotelDTO,
        checkInDate: LocalDate,
        checkOutDate: LocalDate,
        id: Int
    ): Map<RoomTypeDTO, Int> {
        val rooms = hotel.rooms.filter {
            it.occupation == null || it.occupation.checkOutDate <= checkInDate
        }.groupingBy { it.type }.eachCount()
        val reservations = hotel.reservations
        if (reservations.any {
                it.guest.id == id && it.checkInDate <= checkOutDate && it.checkOutDate >= checkInDate
            }) return emptyMap()
        val bookableRooms = rooms.mapValues { (type, count) ->
            max(
                count - reservations.count { reservation ->
                    reservation.roomType == type && reservation.checkInDate <= checkOutDate && reservation.checkOutDate >= checkInDate
                },
                0
            )
        }
        return bookableRooms
    }
}