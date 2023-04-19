package ru.sejapoe.application.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SigningKeyResolver
import io.jsonwebtoken.security.Keys
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.user.Session
import java.security.Key

private fun getKey(subject: String): Key {
    return transaction {
        val session = Session.findById(subject.toInt()) ?: throw AuthorizationError()
        Keys.hmacShaKeyFor(session.sessionKey.bytes)
    }
}

class AuthorizationError : Exception()

@KtorDsl
fun Route.getAuth(function: suspend AuthorizedPipeline.() -> Unit) {
    get {
        authorizationHandler(function)
    }
}

@KtorDsl
fun Route.postAuth(function: suspend AuthorizedPipeline.() -> Unit) {
    post {
        authorizationHandler(function)
    }
}

@KtorDsl
fun Route.deleteAuth(function: suspend AuthorizedPipeline.() -> Unit) {
    delete {
        authorizationHandler(function)
    }
}


@KtorDsl
fun Route.getAuth(path: String, function: suspend AuthorizedPipeline.() -> Unit) {
    get(path) {
        authorizationHandler(function)
    }
}

@KtorDsl
fun Route.postAuth(path: String, function: suspend AuthorizedPipeline.() -> Unit) {
    post(path) {
        authorizationHandler(function)
    }
}

@KtorDsl
fun Route.deleteAuth(path: String, function: suspend AuthorizedPipeline.() -> Unit) {
    delete(path) {
        authorizationHandler(function)
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.authorizationHandler(function: suspend AuthorizedPipeline.() -> Unit) {
    val authorization =
        call.request.header("Authorization") ?: return@authorizationHandler call.respond(HttpStatusCode.Unauthorized)
    val token = authorization.split(" ")[1]
    val parser = Jwts.parserBuilder().setSigningKeyResolver(
        object : SigningKeyResolver {
            override fun resolveSigningKey(header: JwsHeader<*>?, claims: Claims?): Key {
                return getKey(claims!!.subject)
            }


            override fun resolveSigningKey(header: JwsHeader<*>?, plaintext: String?): Key {
                throw AuthorizationError()
            }

        }
    ).build()
    val id = try {
        parser.parseClaimsJws(token).body.subject
    } catch (e: Exception) {
        return@authorizationHandler call.respond(HttpStatusCode.Unauthorized)
    }.toInt()
    val session = transaction {
        Session.findById(id)
    } ?: return@authorizationHandler call.respond(HttpStatusCode.Unauthorized)
    function(AuthorizedPipeline(session, this))
}

class AuthorizedPipeline(val session: Session, private val pipeline: PipelineContext<Unit, ApplicationCall>) {
    val call
        get() = pipeline.call
}