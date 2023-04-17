package ru.sejapoe.application.hotel.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class RoomType(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RoomType>(RoomTypes)

    var hotel by Hotel referencedOn RoomTypes.hotel
    var name by RoomTypes.name
    var price by RoomTypes.price
    var adultsCount by RoomTypes.adultsCount
    var childrenCount by RoomTypes.childrenCount

    fun asDTO() = RoomTypeDTO(id.value, name, price, adultsCount, childrenCount)
}

@Serializable
data class RoomTypeDTO(val id: Int, val name: String, val price: Int, val adultsCount: Int, val childrenCount: Int = 0)

object RoomTypes : IntIdTable() {
    val hotel = reference("hotel", Hotels)
    val name = varchar("name", 128)
    val price = integer("price")
    val adultsCount = integer("adults_count")
    val childrenCount = integer("children_count").default(0)
}