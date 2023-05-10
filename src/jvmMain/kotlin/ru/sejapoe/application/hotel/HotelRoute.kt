package ru.sejapoe.application.hotel

import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.Hotel
import ru.sejapoe.application.hotel.model.HotelDTO
import ru.sejapoe.application.hotel.model.Room
import ru.sejapoe.application.hotel.model.RoomType
import ru.sejapoe.application.utils.exception
import ru.sejapoe.routing.Delete
import ru.sejapoe.routing.Get
import ru.sejapoe.routing.Post
import ru.sejapoe.routing.Route

fun <E> MutableList<E>.popRandom(): E {
    val el = random()
    remove(el)
    return el
}

@Route("/hotel")
object HotelRoute {
    @Post("/populate")
    fun populate() = transaction {
        val roomTypeNames = listOf(
            "Standard",
            "Superior",
            "Ultra",
            "Deluxe",
            "Chef",
            "Lux",
            "Suite",
            "Piece of shit",
            "Closet",
            "Carton box"
        )

        val hotelNames = listOf(
            "Radisson Blu",
            "Hilton",
            "Sheraton",
            "Marriott",
        )

        val hotels = hotelNames.map {
            Hotel.new {
                name = it
            }
        }



        hotels.forEach { hotel ->
            val availableRoomNumbers = (100..999).toMutableList()
            val availableRoomTypeNames = roomTypeNames.toMutableList()
            val roomTypePrice = List((4..7).random()) {
                availableRoomTypeNames.popRandom() to (10..100).random() * 10
            }.sortedBy { it.second }
            roomTypePrice.forEach { (name, price) ->
                val roomType = RoomType.new {
                    this.name = name
                    this.price = price
                    this.adultsCount = 2
                    this.childrenCount = 0
                    this.hotel = hotel
                }

                // creating room
                // random count of rooms
                val count = (40..70).random()
                repeat(count) {
                    Room.new {
                        this.hotel = hotel
                        this.type = roomType
                        this.number = availableRoomNumbers.popRandom()
                    }
                }
            }
        }
    }

    @Get("/{id}")
    fun getHotel(id: Int) = transaction { Hotel.findById(id)?.asDTO() ?: throw HttpStatusCode.NotFound.exception() }

    @Delete("/{id}")
    fun deleteHotel(id: Int) = transaction { Hotel.findById(id)?.delete() ?: throw HttpStatusCode.NotFound.exception() }

    @Get("s")
    fun getHotels() = transaction { Hotel.all().map(Hotel::asLessDTO) }

    @Post("s")
    fun createHotel(hotelDTO: HotelDTO){
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
        throw HttpStatusCode.Created.exception()
    }
}

