package ru.sejapoe.application.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.user.Sessions
import ru.sejapoe.application.user.User
import ru.sejapoe.application.user.Users

object DatabasesFactory {
    fun init() {
        val database = Database.connect(
            "jdbc:postgresql://localhost:5432/test", driver = "org.postgresql.Driver",
            user = "sejapoe"
        )

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(Users)
            SchemaUtils.create(Sessions)
        }
    }
}