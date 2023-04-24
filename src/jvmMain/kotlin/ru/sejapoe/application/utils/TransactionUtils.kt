package ru.sejapoe.application.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.reflect.*
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

suspend fun transaction(call: ApplicationCall, block: TransactionHolder.() -> ResponseInfo<*>) {
    val responseInfo = transaction {
        TransactionHolder(this).block()
    }

    if (responseInfo.message != null) {
        call.respond(responseInfo.status, responseInfo.message.first, responseInfo.message.second)
    } else {
        call.respond(responseInfo.status)
    }
}

data class ResponseInfo<T : Any>(
    val message: Pair<T, TypeInfo>? = null,
    val status: HttpStatusCode = HttpStatusCode.OK,
)

class TransactionHolder(val transaction: Transaction) {
    fun <T : Any> respond(message: T, typeInfo: TypeInfo): ResponseInfo<T> {
        return ResponseInfo(message to typeInfo)
    }

    fun <T : Any> respond(message: T, typeInfo: TypeInfo, status: HttpStatusCode): ResponseInfo<T> {
        return ResponseInfo(message to typeInfo, status)
    }

    fun respond(status: HttpStatusCode): ResponseInfo<*> {
        return ResponseInfo<Void>(status = status)
    }
}