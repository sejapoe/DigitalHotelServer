package ru.sejapoe.application.hotel

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.Rights
import ru.sejapoe.application.hotel.model.Room
import ru.sejapoe.application.user.Session
import ru.sejapoe.application.utils.exception
import ru.sejapoe.routing.Pipeline
import ru.sejapoe.routing.Post
import ru.sejapoe.routing.Provided
import ru.sejapoe.routing.Route

@Suppress("UNUSED")
@Route("/room")
object RoomRoute {
    @Post("/{id}/open")
    fun openRoom(id: Int, @Provided session: Session, @Pipeline pipeline: PipelineContext<Unit, ApplicationCall>) {
        transaction {
            if (session.user.userInfo == null) throw HttpStatusCode.Forbidden.exception()
            val room = Room.findById(id) ?: throw HttpStatusCode.NotFound.exception()
            if (room.occupation?.userSatisfy(
                    session.user,
                    Rights.ACCESS_ROOM
                ) != true
            ) throw HttpStatusCode.Forbidden.exception("loh")
            room.isOpen = true
            FirebaseMessaging.getInstance().sendMulticast(
                MulticastMessage.builder()
                    .addAllTokens(room.occupation!!.roomAccessors.flatMap { it.notificationTokens })
                    .putData("action", "open_room")
                    .putData("room_id", id.toString())
                    .build()
            )
        }
        pipeline.launch {
            delay(5000)
            transaction {
                val room = Room.findById(id)!!
                room.isOpen = false
                FirebaseMessaging.getInstance().sendMulticast(
                    MulticastMessage.builder()
                        .addAllTokens(room.occupation!!.roomAccessors.flatMap { it.notificationTokens })
                        .putData("action", "close_room")
                        .putData("room_id", id.toString())
                        .build()
                )
            }
        }
    }
}