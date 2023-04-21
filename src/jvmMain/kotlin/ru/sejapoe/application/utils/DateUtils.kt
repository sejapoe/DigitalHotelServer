package ru.sejapoe.application.utils

import java.time.LocalDate

fun String.toDate(): LocalDate? {
    return try {
        LocalDate.parse(this)
    } catch (e: Exception) {
        null
    }
}