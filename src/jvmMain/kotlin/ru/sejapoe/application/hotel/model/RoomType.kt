package ru.sejapoe.application.hotel.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.sejapoe.application.db.HotelEntity
import ru.sejapoe.application.db.HotelTable

class RoomType(id: EntityID<Int>) : HotelEntity(id) {
    companion object : IntEntityClass<RoomType>(RoomTypes)

    override var hotel by Hotel referencedOn RoomTypes.hotel
    var name by RoomTypes.name
    var price by RoomTypes.price
    var adultsCount by RoomTypes.adultsCount
    var childrenCount by RoomTypes.childrenCount

    fun asDomain() = RoomTypeDomain(name, price, adultsCount, childrenCount)
}

@Serializable
data class RoomTypeDomain(val name: String, val price: Int, val adultsCount: Int, val childrenCount: Int = 0)

object RoomTypes : HotelTable() {
    val name = varchar("name", 128)
    val price = integer("price")
    val adultsCount = integer("adults_count")
    val childrenCount = integer("children_count").default(0)
}