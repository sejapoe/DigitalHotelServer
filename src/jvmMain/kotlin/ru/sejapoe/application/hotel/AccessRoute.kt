package ru.sejapoe.application.hotel

import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.user.Session
import ru.sejapoe.application.utils.exception
import ru.sejapoe.routing.Get
import ru.sejapoe.routing.Provided
import ru.sejapoe.routing.Route

@Suppress("UNUSED")
@Route("/access")
object AccessRoute {
    @Get("es")
    fun getAccesses(@Provided session: Session) = transaction {
        if (session.user.userInfo == null) throw HttpStatusCode.Forbidden.exception()
        session.user.roomAccesses
    }
}