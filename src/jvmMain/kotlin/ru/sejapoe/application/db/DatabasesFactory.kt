package ru.sejapoe.application.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.Hotels
import ru.sejapoe.application.hotel.model.Reservations
import ru.sejapoe.application.hotel.model.RoomTypes
import ru.sejapoe.application.hotel.model.Rooms
import ru.sejapoe.application.user.Sessions
import ru.sejapoe.application.user.Users

private const val productionUrl = "jdbc:postgresql://db:5432/test?user=postgres"
private const val developmentUrl = "jdbc:postgresql://localhost:5432/test"

object DatabasesFactory {
    fun init(isProduction: Boolean) {
        Database.connect(
            if (isProduction) productionUrl else developmentUrl,
            driver = "org.postgresql.Driver",
            user = "sejapoe"
        )

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(Users)
            SchemaUtils.create(Sessions)
            SchemaUtils.create(Hotels)
            SchemaUtils.create(Reservations)
            SchemaUtils.create(Rooms)
            SchemaUtils.create(RoomTypes)
        }
    }
}