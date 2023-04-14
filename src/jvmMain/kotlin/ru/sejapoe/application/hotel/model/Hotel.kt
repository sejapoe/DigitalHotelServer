package ru.sejapoe.application.hotel.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class Hotel(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Hotel>(Hotels)

    var name by Hotels.name
    val roomTypes by RoomType referrersOn RoomTypes.hotel

    fun asDomain() = HotelDomain(id.value, name, roomTypes.map { it.asDomain() })
}

@Serializable
data class HotelDomain(val id: Int?, val name: String, val roomTypes: List<RoomTypeDomain>)


object Hotels : IntIdTable() {
    val name = varchar("name", 128)
}