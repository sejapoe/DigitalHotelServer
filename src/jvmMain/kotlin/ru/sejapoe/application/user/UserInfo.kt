package ru.sejapoe.application.user

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

class UserInfo(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserInfo>(UserInfos)

    var firstName by UserInfos.firstName
    var lastName by UserInfos.lastName
    var parentheses by UserInfos.parentheses
    var phoneNumber by UserInfos.phoneNumber
    var birthDate by UserInfos.birthDate
    var sex by UserInfos.sex
    val documents by Document referrersOn Documents.userInfoId
    val fullName
        get() = "$lastName $firstName $parentheses"

    fun asDTO() = UserInfoDTO(id.value, firstName, lastName, parentheses, phoneNumber, birthDate, sex)
}

@Serializable
data class UserInfoDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val parentheses: String?,
    val phoneNumber: String,
    @Contextual val birthDate: LocalDate,
    val sex: Sex
)

object UserInfos : IntIdTable() {
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val parentheses = varchar("parentheses", 255).nullable()
    val phoneNumber = varchar("phone_number", 32)
    val birthDate = date("birth_date")
    val sex = enumeration<Sex>("sex")
}

enum class Sex {
    MALE, FEMALE
}
