package ru.sejapoe.application.hotel

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.*
import ru.sejapoe.application.utils.*
import ru.sejapoe.routing.Get
import ru.sejapoe.routing.Response
import ru.sejapoe.routing.Route
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.max

@Route("")
object HotelRouting {
    @Route("/hotel")
    object HotelRouting {
        @Get("/{id}")
        fun getHotel(id: Int): Response<HotelDTO> {
            val hotel =
                transaction { Hotel.findById(id)?.asDTO() } ?: throw NotFoundException()
            return Response(data = hotel)
        }
    }
}

fun Routing.hotelRouting() {
    route("/hotel") {
        get("/{id?}") {
            val id =
                this.call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val hotel =
                transaction { Hotel.findById(id)?.asDTO() } ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(hotel)
        }

        delete("/{id?}") {
            val id =
                this.call.parameters["id"]?.toInt() ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val isSuccess = transaction { Hotel.findById(id)?.delete() != null }
            call.respond(if (isSuccess) HttpStatusCode.OK else HttpStatusCode.NotFound)
        }
    }
    route("/hotels") {
        get {
            val hotels = transaction { Hotel.all().map(Hotel::asLessDTO) }
            call.respond(hotels)
        }

        post {
            val hotelDTO = this.call.receive<HotelDTO>()
            transaction {
                val hotel = Hotel.new {
                    name = hotelDTO.name
                }

                hotelDTO.roomTypes.forEach {
                    RoomType.new {
                        name = it.name
                        price = it.price
                        adultsCount = it.adultsCount
                        childrenCount = it.childrenCount
                        this.hotel = hotel
                    }
                }
            }
            call.respond(HttpStatusCode.Created)
        }
    }

    route("/book/{hotelId}/{checkIn}/{checkOut}") {
        getAuth {
            val hotelId =
                this.call.parameters["hotelId"]?.toIntOrNull() ?: return@getAuth call.respond(HttpStatusCode.BadRequest)
            val checkInDate =
                this.call.parameters["checkIn"]?.toDate() ?: return@getAuth call.respond(HttpStatusCode.BadRequest)
            val checkOutDate =
                this.call.parameters["checkOut"]?.toDate() ?: return@getAuth call.respond(HttpStatusCode.BadRequest)
            val hotel =
                transaction { Hotel.findById(hotelId)?.asDTO() } ?: return@getAuth call.respond(HttpStatusCode.NotFound)
            val bookableRooms =
                getBookableRooms(
                    hotel,
                    checkInDate,
                    checkOutDate,
                    transaction { session.user.id.value }).map { (type, count) -> BookableRoom(count, type) }
            if (bookableRooms.isEmpty()) return@getAuth call.respond(HttpStatusCode.NotFound)
            call.respond(bookableRooms)
        }

        postAuth("/{roomTypeId}") {
            val hotelId =
                this.call.parameters["hotelId"]?.toIntOrNull()
                    ?: return@postAuth call.respond(HttpStatusCode.BadRequest)
            val roomTypeId =
                this.call.parameters["roomTypeId"]?.toIntOrNull()
                    ?: return@postAuth call.respond(HttpStatusCode.BadRequest)
            val checkOutDate =
                this.call.parameters["checkOut"]?.toDate() ?: return@postAuth call.respond(HttpStatusCode.BadRequest)
            val checkInDate =
                this.call.parameters["checkIn"]?.toDate() ?: return@postAuth call.respond(HttpStatusCode.BadRequest)
            val hotel =
                transaction { Hotel.findById(hotelId)?.asDTO() }
                    ?: return@postAuth call.respond(HttpStatusCode.NotFound)

            val bookableRooms =
                getBookableRooms(hotel, checkInDate, checkOutDate, transaction { session.user.id.value })
            val count =
                bookableRooms.mapKeys { it.key.id }[roomTypeId] ?: return@postAuth call.respond(HttpStatusCode.NotFound)
            if (count <= 0) return@postAuth call.respond(HttpStatusCode.NotFound)
            transaction {
                Booking.new {
                    this.hotel = Hotel[hotelId]
                    this.checkInDate = checkInDate
                    this.checkOutDate = checkOutDate
                    roomType = RoomType[roomTypeId]
                    guest = session.user
                }
            }
            call.respond(HttpStatusCode.OK)
        }

    }

    route("/booking/{id}") {
        postAuth("/pay") {
            // TODO: implement payment, now we just like it is paid
            val id =
                this.call.parameters["id"]?.toIntOrNull() ?: return@postAuth call.respond(HttpStatusCode.BadRequest)

            transaction(call) {
                val booking = Booking.findById(id) ?: return@transaction respond(HttpStatusCode.NotFound)
                if (booking.guest.id != session.user.id) return@transaction respond(HttpStatusCode.Forbidden)
                booking.payment = Payment.new {
                    this.user = session.user
                    this.amount = booking.roomType.price
                    this.timestamp = LocalDateTime.now()
                }
                respond(HttpStatusCode.OK)
            }
        }

        getAuth {
            val id =
                this.call.parameters["id"]?.toIntOrNull() ?: return@getAuth call.respond(HttpStatusCode.BadRequest)
            transaction(call) {
                val booking = Booking.findById(id) ?: return@transaction respond(HttpStatusCode.NotFound)
                if (booking.guest.id != session.user.id) return@transaction respond(HttpStatusCode.Forbidden)
                respond(booking.asDTO(), typeInfo<BookingDTO>())
            }
        }

        deleteAuth {
            val id =
                this.call.parameters["id"]?.toIntOrNull() ?: return@deleteAuth call.respond(HttpStatusCode.BadRequest)
            transaction(call) {
                val booking = Booking.findById(id) ?: return@transaction respond(HttpStatusCode.NotFound)
                if (booking.guest.id != session.user.id) return@transaction respond(HttpStatusCode.Forbidden)
                booking.delete()
                respond(HttpStatusCode.OK)
            }
        }
    }

    route("/bookings") {
        getAuth {
            transaction(call) {
                respond(session.user.bookings.map(Booking::asDTO), typeInfo<List<BookingDTO>>())
            }
        }
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