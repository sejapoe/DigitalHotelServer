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
    fun getHotel(id: Int): Response<HotelDTO> {
        val hotel =
            transaction { Hotel.findById(id)?.asDTO() } ?: throw HttpStatusCode.NotFound.exception()
        return Response(data = hotel)
    }

    @Delete("/{id}")
    fun deleteHotel(id: Int) = transaction { Hotel.findById(id)?.delete() ?: throw HttpStatusCode.NotFound.exception() }

    @Get("s")
    fun getHotels(): Response<List<HotelLessDTO>> {
        val hotels = transaction { Hotel.all().map(Hotel::asLessDTO) }
        return Response(data = hotels)
    }

    @Post("s")
    fun createHotel(hotelDTO: HotelDTO): Response<Unit> {
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
        return Response(HttpStatusCode.Created)
    }
}

