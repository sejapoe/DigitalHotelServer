package ru.sejapoe.application.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.user.Sessions
import ru.sejapoe.application.user.Users

object DatabasesFactory {
    fun init() {
        Database.connect(
            "jdbc:postgresql://db:5432/test?user=postgres", driver = "org.postgresql.Driver",
            user = "sejapoe"
        )

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(Users)
            SchemaUtils.create(Sessions)
        }
    }
}