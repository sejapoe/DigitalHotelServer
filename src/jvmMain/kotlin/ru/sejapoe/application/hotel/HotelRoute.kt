package ru.sejapoe.application.hotel

import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.Hotel
import ru.sejapoe.application.hotel.model.HotelDTO
import ru.sejapoe.application.hotel.model.HotelLessDTO
import ru.sejapoe.application.hotel.model.RoomType
import ru.sejapoe.application.utils.exception
import ru.sejapoe.routing.*

@Route("/hotel")
object HotelRoute {
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

