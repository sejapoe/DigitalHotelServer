package ru.sejapoe.application.hotel

import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.Occupation
import ru.sejapoe.application.user.Session
import ru.sejapoe.application.utils.exception
import ru.sejapoe.routing.Get
import ru.sejapoe.routing.Provided
import ru.sejapoe.routing.Route

@Suppress("UNUSED")
@Route("/occupation")
object OccupationRoute {
    @Get("s")
    fun getOccupations(@Provided session: Session) = transaction {
        if (session.user.userInfo == null) throw HttpStatusCode.Forbidden.exception()
        session.user.occupations.map(Occupation::asDTO)
    }
}