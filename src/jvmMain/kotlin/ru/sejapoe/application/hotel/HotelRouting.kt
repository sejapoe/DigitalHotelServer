package ru.sejapoe.application.hotel

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.*
import ru.sejapoe.application.utils.postAuth
import ru.sejapoe.application.utils.toDate
import java.time.LocalDate
import kotlin.math.max

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

    postAuth("/book/{hotelId}/{checkIn}/{checkOut}/{roomTypeId}") {
        val hotelId =
            this.call.parameters["hotelId"]?.toIntOrNull() ?: return@postAuth call.respond(HttpStatusCode.BadRequest)
        val roomTypeId =
            this.call.parameters["roomTypeId"]?.toIntOrNull() ?: return@postAuth call.respond(HttpStatusCode.BadRequest)
        val checkOutDate =
            this.call.parameters["checkOut"]?.toDate() ?: return@postAuth call.respond(HttpStatusCode.BadRequest)
        val checkInDate =
            this.call.parameters["checkIn"]?.toDate() ?: return@postAuth call.respond(HttpStatusCode.BadRequest)
        val hotel =
            transaction { Hotel.findById(hotelId)?.asDTO() } ?: return@postAuth call.respond(HttpStatusCode.NotFound)

        val bookableRooms = getBookableRooms(hotel, checkInDate, checkOutDate)
        val count =
            bookableRooms.mapKeys { it.key.id }[roomTypeId] ?: return@postAuth call.respond(HttpStatusCode.NotFound)
        if (count <= 0) return@postAuth call.respond(HttpStatusCode.NotFound)
        transaction {
            Reservation.new {
                this.hotel = Hotel[hotelId]
                this.checkInDate = checkInDate
                this.checkOutDate = checkOutDate
                this.roomType = RoomType[roomTypeId]
                this.guest = this@postAuth.session.user
            }
        }
        call.respond(HttpStatusCode.OK)
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
                transaction { Hotel.findById(hotelId)?.asDTO() } ?: return@get call.respond(HttpStatusCode.NotFound)
            val bookableRooms =
                getBookableRooms(hotel, checkInDate, checkOutDate).map { (type, count) -> BookableRoom(count, type) }
            call.respond(bookableRooms)
        }
    }
}

private fun getBookableRooms(
    hotel: HotelDTO,
    checkInDate: LocalDate,
    checkOutDate: LocalDate
): Map<RoomTypeDTO, Int> {
    val rooms = hotel.rooms.filter {
        it.occupation == null || it.occupation.checkOutDate <= checkInDate
    }.groupingBy { it.type }.eachCount()
    val reservations = hotel.reservations
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