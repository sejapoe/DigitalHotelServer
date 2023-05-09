package ru.sejapoe.application.hotel

import com.google.firebase.messaging.AndroidNotification
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
import ru.sejapoe.application.utils.notify
import ru.sejapoe.routing.*

@Suppress("UNUSED")
@Route("/room/{id}/share")
object AccessSharingRoute {
    @Get("s")
    fun getSharedAccesses(id: Int, @Provided session: Session) = transaction {
        val occupation = getOccupation(id, session)
        occupation.sharedAccesses.map(SharedAccess::asDTO)
    }

    @Post("s")
    fun shareAccess(id: Int, @Body usernames: List<String>, @Provided session: Session) = transaction {
        val occupation = getOccupation(id, session)
        val targetUsers = usernames.map {
            User.find { Users.username eq it }.firstOrNull() ?: throw HttpStatusCode.NotFound.exception()
        }
        if (occupation.sharedAccesses.map { it.user.id }.toSet().intersect(targetUsers.map(User::id).toSet())
                .isNotEmpty()
        ) throw HttpStatusCode.Conflict.exception()
        targetUsers.forEach { targetUser ->
            SharedAccess.new {
                user = targetUser
                this.occupation = occupation
            }
        }
        AndroidNotification.builder()
            .setTitleLocalizationKey("access_granted_title")
            .setBodyLocalizationKey("access_granted_body")
            .addBodyLocalizationArg(session.user.asLessDTO().fullName ?: "unknown")
            .addBodyLocalizationArg(occupation.room.number.toString())
            .addBodyLocalizationArg(occupation.room.hotel.name)
            .build()
            .notify(targetUsers.flatMap(User::notificationTokens))
    }

    @Put("/{shareId}")
    fun editShare(id: Int, shareId: Int, @Body edit: AccessShareEdit, @Provided session: Session) = transaction {
        val occupation = getOccupation(id, session)
        val share = SharedAccess.findById(shareId) ?: throw HttpStatusCode.NotFound.exception()
        if (occupation.sharedAccesses.all { it != share }) throw HttpStatusCode.Forbidden.exception()

        share.budget = edit.budget
        share.rights = RightsComposition(edit.rights)
    }

    @Delete("/{shareId}")
    fun editShare(id: Int, shareId: Int, @Provided session: Session) = transaction {
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