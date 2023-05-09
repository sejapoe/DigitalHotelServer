package ru.sejapoe.application.hotel

import io.ktor.http.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.RoomLessDTO
import ru.sejapoe.application.hotel.model.SharedAccess
import ru.sejapoe.application.hotel.model.SharedAccesses
import ru.sejapoe.application.user.Session
import ru.sejapoe.application.utils.exception
import ru.sejapoe.routing.Get
import ru.sejapoe.routing.Provided
import ru.sejapoe.routing.Route
import java.time.LocalDate

@Suppress("UNUSED")
@Route("/access")
object AccessRoute {
    @Get("es")
    fun getAccesses(@Provided session: Session) = transaction {
        if (session.user.userInfo == null) throw HttpStatusCode.Forbidden.exception()
        session.user.occupations.map { RoomAccess(it.room.asLessDTO(), it.checkInDate, it.checkOutDate, 255) }
            .toList() + SharedAccess.find { SharedAccesses.user eq session.user.id }.map {
            RoomAccess(
                it.occupation.room.asLessDTO(),
                it.occupation.checkInDate,
                it.occupation.checkOutDate,
                it.rights.value
            )
        }
    }

    @Serializable
    data class RoomAccess(
        val room: RoomLessDTO,
        @Contextual val checkInDate: LocalDate,
        @Contextual val checkOutDate: LocalDate,
        val rights: Short // 255 if room owner
    )
}