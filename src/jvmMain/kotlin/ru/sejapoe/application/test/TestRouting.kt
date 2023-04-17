package ru.sejapoe.application.test

import com.google.firebase.messaging.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.user.Session
import ru.sejapoe.application.utils.postAuth

fun Routing.testRouting() {
    post("/test2") {
        val tokens = transaction {
            Session.all().mapNotNull { it.notificationToken }
        }
        val message = MulticastMessage.builder()
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setNotification(
                        AndroidNotification.builder()
                            .setTitle("Test")
                            .setBody("Test")
                            .setPriority(AndroidNotification.Priority.HIGH)
                            .build()
                    ).build()
            )
            .addAllTokens(tokens)
            .build()
        val x = FirebaseMessaging.getInstance().sendMulticast(message).responses.map {
            it.isSuccessful
        }
        call.respond(HttpStatusCode.OK, x)
    }

    postAuth("/test") {
        if (session.notificationToken == null) return@postAuth call.respond(HttpStatusCode.NotFound)
        val message = Message.builder()
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setNotification(
                        AndroidNotification.builder()
                            .setTitle("Test")
                            .setBody("Test")
                            .setPriority(AndroidNotification.Priority.HIGH)
                            .build()
                    ).build()
            )
            .setToken(session.notificationToken)
            .build()
        Thread.sleep(5000)
        FirebaseMessaging.getInstance().send(message)
        call.respond(HttpStatusCode.OK)
    }
}