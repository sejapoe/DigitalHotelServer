package ru.sejapoe.application.hotel

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.*
import ru.sejapoe.application.utils.toDate
import kotlin.math.max

fun Routing.hotelRouting() {
    route("/hotel") {
        get("/{id?}") {
            val id =
                this.call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val hotel =
                transaction { Hotel.findById(id)?.asDomain() } ?: return@get call.respond(HttpStatusCode.NotFound)
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
            val hotels = transaction { Hotel.all().map(Hotel::asDomain) }
            call.respond(hotels)
        }

        post {
            val hotelDomain = this.call.receive<HotelDomain>()
            transaction {
                val hotel = Hotel.new {
                    name = hotelDomain.name
                }

                hotelDomain.roomTypes.forEach {
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
    route("/reservations") {
        get("/{hotelId}/{checkIn}/{checkOut}") {
            val hotelId =
                this.call.parameters["hotelId"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val checkInDate =
                this.call.parameters["checkIn"]?.toDate() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val checkOutDate =
                this.call.parameters["checkOut"]?.toDate() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val hotel =
                transaction { Hotel.findById(hotelId)?.asDomain() } ?: return@get call.respond(HttpStatusCode.NotFound)
            val rooms = transaction {
                Room.find {
                    Rooms.hotel eq hotel.id and
                            (Rooms.occupation.isNull() or
                                    ((Occupations.id eq Rooms.occupation) and (Occupations.checkOutDate lessEq checkInDate)))
                }
            }.groupingBy { it.type }.eachCount()
            val reservations = transaction { Reservation.find { Reservations.hotel eq hotel.id } }
            val bookableRooms = rooms.mapValues { (type, count) ->
                max(
                    count - reservations.count { reservation ->
                        reservation.roomType == type && reservation.checkInDate <= checkOutDate && reservation.checkOutDate >= checkInDate
                    },
                    0
                )
            }.map { (type, count) -> BookableRoom(count, type.asDomain()) }
            call.respond(bookableRooms)
        }
    }
}