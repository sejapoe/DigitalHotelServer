package ru.sejapoe.application.user

import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.utils.exception
import ru.sejapoe.routing.Get
import ru.sejapoe.routing.Post
import ru.sejapoe.routing.Provided
import ru.sejapoe.routing.Route
import java.math.BigInteger

@Route
object UserRoute {
    @Route("/register")
    object Register {
        @Post("/start")
        fun start(data: Pair<String, String>) =
            try {
                startRegistration(data).asBase64()
            } catch (e: UserAlreadyExists) {
                throw HttpStatusCode.Found.exception()
            } catch (e: Exception) {
                e.printStackTrace()
                throw HttpStatusCode.InternalServerError.exception(e.localizedMessage)
            }

        @Post("/finish")
        fun finish(data: Pair<String, BigInteger>) = finishRegistration(data)
    }

    @Route("/login")
    object Login {
        @Post("/start")
        fun start(data: Pair<String, BigInteger>) = try {
            login(data)
        } catch (e: NoSuchUser) {
            throw HttpStatusCode.NotFound.exception()
        } catch (e: Exception) {
            e.printStackTrace()
            throw HttpStatusCode.InternalServerError.exception(e.localizedMessage)
        }

        @Post("/finish")
        fun finish(data: String) = try {
            confirm(data)
        } catch (e: WrongPasswordException) {
            throw HttpStatusCode.Forbidden.exception()
        }
    }

    @Get("/ping")
    fun ping(@Provided session: Session) =
        transaction { if (session.user.userInfo == null) throw HttpStatusCode.Forbidden.exception() }

    @Post("/subscribe")
    fun subscribe(data: String, @Provided session: Session) = transaction { session.notificationToken = data }

    @Post("/logout")
    fun logout(@Provided session: Session) = transaction { session.delete() }

    @Post("/user/info")
    fun setUserInfo(data: UserInfoDTO, @Provided session: Session) = transaction {
        session.user.userInfo = UserInfo.new {
            this.firstName = data.firstName
            this.lastName = data.lastName
            this.parentheses = data.parentheses
            this.phoneNumber = data.phoneNumber
            this.birthDate = data.birthDate
            this.sex = data.sex
        }
    }
}

