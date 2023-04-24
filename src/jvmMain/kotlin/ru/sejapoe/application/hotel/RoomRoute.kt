package ru.sejapoe.application.hotel

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
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
            val room = Room.findById(id) ?: throw HttpStatusCode.NotFound.exception()
            if (room.occupation?.guest?.id != session.user.id) throw HttpStatusCode.Forbidden.exception()
            room.isOpen = true
        }
        pipeline.launch {
            delay(5000)
            transaction {
                Room.findById(id)!!.isOpen = false
            }
            if (session.notificationToken != null) {
                FirebaseMessaging.getInstance().send(
                    Message.builder()
                        .setToken(session.notificationToken)
                        .putData("action", "close_room")
                        .putData("room_id", id.toString())
                        .build()
                )
            }
        }
    }
}