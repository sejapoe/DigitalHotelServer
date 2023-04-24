package ru.sejapoe.application.utils

import io.ktor.http.*

class HttpException(val code: HttpStatusCode, message: String = code.description) : Exception(message) {
    override fun toString(): String {
        return "${code.value}: $message"
    }
}

fun HttpStatusCode.exception(message: String = description) = HttpException(this, message)