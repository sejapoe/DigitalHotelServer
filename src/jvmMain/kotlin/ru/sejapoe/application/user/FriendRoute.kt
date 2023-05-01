package ru.sejapoe.application.user

import com.google.firebase.messaging.AndroidNotification
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.utils.exception
import ru.sejapoe.application.utils.notify
import ru.sejapoe.routing.*

@Route("/friend")
object FriendRoute {
    @Post("/request") // accept or create friend request
    fun request(@Body targetUsername: String, @Provided session: Session) = transaction {
        val target =
            User.find { Users.username eq targetUsername }.firstOrNull() ?: throw HttpStatusCode.NotFound.exception()
        if ((session.user.friends + session.user.outcomeFriendRequests).any { it.from == target || it.to == target }) throw HttpStatusCode.Found.exception()
        val incomeFriendRequest = session.user.incomeFriendRequests.firstOrNull { it.from == target }
        if (incomeFriendRequest != null) {
            incomeFriendRequest.accepted = true
            // notify
            AndroidNotification.builder()
                .setTitleLocalizationKey("friend_request_accepted_title")
                .setBodyLocalizationKey("friend_request_accepted_body")
                .addBodyLocalizationArg(session.user.asLessDTO().fullName ?: "unknown")
                .build()
                .notify(incomeFriendRequest.from.notificationTokens)
            return@transaction
        }
        Friendship.new {
            from = session.user
            to = target
        }
        AndroidNotification.builder()
            .setTitleLocalizationKey("new_friend_request_title")
            .setTitleLocalizationKey("new_friend_request_body")
            .addBodyLocalizationArg(session.user.asLessDTO().fullName ?: "unknown")
            .build()
            .notify(target.notificationTokens)
    }

    @Get("/requests")
    fun getRequests(@Provided session: Session) = transaction {
        FriendRequests(
            session.user.incomeFriendRequests.map { it.from.asLessDTO() },
            session.user.outcomeFriendRequests.map { it.to.asLessDTO() })
    }

    @Get("s")
    fun getFriends(@Provided session: Session) = transaction {
        session.user.friends.map { if (it.from == session.user) it.to.asLessDTO() else it.from.asLessDTO() }
    }

    @Serializable
    data class FriendRequests(val income: List<UserLessDTO>, val outcome: List<UserLessDTO>)
}