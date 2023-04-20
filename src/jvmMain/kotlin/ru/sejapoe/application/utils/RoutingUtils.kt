package ru.sejapoe.application.utils

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.user.Session
import ru.sejapoe.routing.Provider
import java.security.Key

private fun getKey(subject: String): Key {
    return transaction {
        val session = Session.findById(subject.toInt()) ?: throw HttpStatusCode.Unauthorized.exception()
        Keys.hmacShaKeyFor(session.sessionKey.bytes)
    }
}

object SessionProvider : Provider<Session> {
    override suspend fun provide(call: ApplicationCall): Session {
        val authorization =
            call.request.header("Authorization") ?: throw HttpStatusCode.Unauthorized.exception()
        val token = authorization.split(" ")[1]
        val parser = Jwts.parserBuilder().setSigningKeyResolver(
            object : SigningKeyResolver {
                override fun resolveSigningKey(header: JwsHeader<*>?, claims: Claims?): Key {
                    return getKey(claims!!.subject)
                }

                override fun resolveSigningKey(header: JwsHeader<*>?, plaintext: String?): Key {
                    throw UnsupportedJwtException("Unsupported JWT")
                }
            }
        ).build()
        val id = try {
            parser.parseClaimsJws(token).body.subject
        } catch (e: Exception) {
            throw HttpStatusCode.Unauthorized.exception()
        }.toInt()
        val session = transaction {
            Session.findById(id)
        } ?: throw HttpStatusCode.Unauthorized.exception()
        return session
    }
}