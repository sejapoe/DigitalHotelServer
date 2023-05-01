package ru.sejapoe.application.user

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.ZoneOffset

class Friendship(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Friendship>(Friendships)

    var from by User referencedOn Friendships.from
    var to by User referencedOn Friendships.to
    var accepted by Friendships.accepted
    val timestamp by Friendships.timestamp
    fun asDTO() = FriendshipDTO(id.value, from.asDTO(), to.asDTO(), accepted, timestamp.toEpochSecond(ZoneOffset.UTC))
}

@Serializable
data class FriendshipDTO(val id: Int, val from: UserDTO, val to: UserDTO, val accepted: Boolean, val timestamp: Long)

object Friendships : IntIdTable() {
    val from = reference("from", Users)
    val to = reference("to", Users)
    val accepted = bool("accepted").default(false)
    val timestamp = datetime("timestamp").defaultExpression(CurrentDateTime)
}