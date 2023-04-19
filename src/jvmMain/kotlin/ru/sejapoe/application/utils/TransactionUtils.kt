package ru.sejapoe.application.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

suspend fun transaction(call: ApplicationCall, block: TransactionHolder.() -> ResponseInfo<*>) {
    val responseInfo = transaction {
        TransactionHolder(this).block()
    }

    if (responseInfo.message != null) {
        call.respond(responseInfo.status, responseInfo.message)
    } else {
        call.respond(responseInfo.status)
    }
}

data class ResponseInfo<T>(
    val message: T? = null,
    val status: HttpStatusCode = HttpStatusCode.OK,
)

class TransactionHolder(val transaction: Transaction) {
    fun <T> respond(message: T): ResponseInfo<T> {
        return ResponseInfo(message)
    }

    fun <T> respond(message: T, status: HttpStatusCode): ResponseInfo<T> {
        return ResponseInfo(message, status)
    }

    fun respond(status: HttpStatusCode): ResponseInfo<*> {
        return ResponseInfo<Void>(status = status)
    }
}