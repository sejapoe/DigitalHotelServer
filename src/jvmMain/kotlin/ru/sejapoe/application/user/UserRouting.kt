package ru.sejapoe.application.user

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.utils.postAuth
import java.math.BigInteger

fun Routing.userRouting() {
    route("/register") {
        post("/start") {
            try {
                val data = call.receive<Pair<String, String>>()
                val bitArray256 = startRegistration(data)
                call.respond(HttpStatusCode.OK, bitArray256.asBase64())
            } catch (e: UserAlreadyExists) {
                call.respond(HttpStatusCode.Found)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        post("/finish") {
            val data = call.receive<Pair<String, BigInteger>>()
            finishRegistration(data)
            call.respond(HttpStatusCode.OK)
        }
    }

    route("/login") {
        post("/start") {
            try {
                val data = call.receive<Pair<String, BigInteger>>()
                val login = login(data)
                call.respond(HttpStatusCode.OK, login)
            } catch (e: NoSuchUser) {
                call.respond(HttpStatusCode.NotFound)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        post("/finish") {
            try {
                val data = call.receive<String>()
                call.respond(HttpStatusCode.OK, confirm(data).toString())
            } catch (e: WrongPasswordException) {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }

    postAuth("/logout") {
        transaction {
            session.delete()
        }
        call.respond(HttpStatusCode.OK)
    }
}


