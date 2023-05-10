package ru.sejapoe.application.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.hotel.model.*
import ru.sejapoe.application.user.Friendships
import ru.sejapoe.application.user.Sessions
import ru.sejapoe.application.user.UserInfos
import ru.sejapoe.application.user.Users

private const val productionUrl = "jdbc:postgresql://db:5432/test?user=postgres"
private const val developmentUrl = "jdbc:postgresql://localhost:5432/test"

object DatabasesFactory {
    fun init(isProduction: Boolean) {
        val database = Database.connect(
            if (isProduction) productionUrl else developmentUrl,
            driver = "org.postgresql.Driver",
            user = "sejapoe"
        )

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.createMissingTablesAndColumns(
                Users,
                Sessions,
                Payments,
                Hotels,
                Bookings,
                Rooms,
                RoomTypes,
                Occupations,
                Friendships,
                UserInfos,
                SharedAccesses
            ) // TODO: remove when production
        }
    }
}