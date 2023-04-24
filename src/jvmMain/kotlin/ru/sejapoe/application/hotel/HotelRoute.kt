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

@Route("/hotel")
object HotelRoute {
    @Post("/populate")
    fun populate() = transaction {
        val roomTypePrice = listOf(
            "Standard" to 100,
            "Superior" to 200,
            "Deluxe" to 300,
            "Suite" to 400
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
                        this.number = it
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

