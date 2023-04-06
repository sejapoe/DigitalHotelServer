package ru.sejapoe.application.user

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table.Dual.default
import ru.sejapoe.application.utils.BitArray256
import java.time.LocalDateTime
import java.time.ZoneOffset

class Session(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Session>(Sessions)

    var user by User referencedOn Sessions.user
    var sessionKey by Sessions.sessionKey.transform({ it.bytes }, { BitArray256(it) })
    var createdAt: LocalDateTime by Sessions.createdAt.default(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
        .transform({ it.toEpochSecond(ZoneOffset.UTC) }, { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) })
}

object Sessions : IntIdTable() {
    val user = reference("user", Users)
    val sessionKey = binary("session_key", 32)
    val createdAt = long("created_at")
}
