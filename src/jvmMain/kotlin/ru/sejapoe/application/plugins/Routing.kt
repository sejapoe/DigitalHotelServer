package ru.sejapoe.application.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import kotlinx.html.HTML
import ru.sejapoe.application.hotel.hotelRouting
import ru.sejapoe.application.index
import ru.sejapoe.application.user.userRouting

fun Application.configureRouting() {
    routing {
        // rest api
        userRouting()
        hotelRouting()

        // front
        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }
        static("/static") {
            resources()
        }
    }
}