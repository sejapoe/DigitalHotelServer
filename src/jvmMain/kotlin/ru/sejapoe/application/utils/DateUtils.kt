package ru.sejapoe.application.utils

import io.ktor.http.*
import ru.sejapoe.routing.ParameterConverter
import java.time.LocalDate

fun String.toDate(): LocalDate? {
    return try {
        LocalDate.parse(this)
    } catch (e: Exception) {
        null
    }
}

object LocalDateConverter : ParameterConverter<LocalDate> {
    override fun fromString(from: String) =
        from.toDate() ?: throw HttpStatusCode.BadRequest.exception("Wrong date format")

    override fun toString(from: LocalDate) = from.toString()

}