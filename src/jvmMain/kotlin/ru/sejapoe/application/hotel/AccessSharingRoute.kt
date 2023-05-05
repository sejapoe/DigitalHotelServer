package ru.sejapoe.application.hotel

import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.Occupation
import ru.sejapoe.application.hotel.model.RightsComposition
import ru.sejapoe.application.hotel.model.Room
import ru.sejapoe.application.hotel.model.SharedAccess
import ru.sejapoe.application.user.Session
import ru.sejapoe.application.user.User
import ru.sejapoe.application.user.Users
import ru.sejapoe.application.utils.exception
import ru.sejapoe.routing.*

@Suppress("UNUSED")
@Route("/room/{id}/share")
object AccessSharingRoute {
    @Post
    fun shareAccess(@Provided session: Session, id: Int, @Body username: String) = transaction {
        val occupation = getOccupation(id, session)
        val targetUser =
            User.find { Users.username eq username }.firstOrNull() ?: throw HttpStatusCode.NotFound.exception()
        if (occupation.sharedAccesses.any { it.user == targetUser }) throw HttpStatusCode.Conflict.exception()

        SharedAccess.new {
            user = targetUser
            this.occupation = occupation
        }
    }

    @Put("/{shareId}")
    fun editShare(@Provided session: Session, id: Int, shareId: Int, @Body edit: AccessShareEdit) = transaction {
        val occupation = getOccupation(id, session)
        val share = SharedAccess.findById(shareId) ?: throw HttpStatusCode.NotFound.exception()
        if (occupation.sharedAccesses.all { it != share }) throw HttpStatusCode.Forbidden.exception()

        share.budget = edit.budget
        share.rights = RightsComposition(edit.rights)
    }

    @Delete("/{shareId}")
    fun editShare(@Provided session: Session, id: Int, shareId: Int) = transaction {
        val occupation = getOccupation(id, session)
        val share = SharedAccess.findById(shareId) ?: throw HttpStatusCode.NotFound.exception()
        if (occupation.sharedAccesses.all { it != share }) throw HttpStatusCode.Forbidden.exception()

        share.delete()
    }

    private fun getOccupation(
        id: Int,
        session: Session
    ): Occupation {
        val room = Room.findById(id) ?: throw HttpStatusCode.NotFound.exception()
        val occupation = room.occupation ?: throw HttpStatusCode.Forbidden.exception()
        if (occupation.guest != session.user) throw HttpStatusCode.Forbidden.exception()
        return occupation
    }

    @Serializable
    data class AccessShareEdit(val rights: Short, val budget: Int?)
}