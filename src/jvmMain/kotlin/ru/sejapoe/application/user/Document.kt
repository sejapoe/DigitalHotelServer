package ru.sejapoe.application.user

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

class Document(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Document>(Documents)

    var userInfo by UserInfo referencedOn Documents.userInfoId
    var type by Documents.type
    var number by Documents.number
    var series by Documents.series
    var issuedBy by Documents.issuedBy
    var issuedDate by Documents.issuedDate
    var validUntil by Documents.validUntil
    var photo by Documents.photo
    var country by Documents.country
    fun asDTO() = DocumentDTO(id.value, type, number, series, issuedBy, issuedDate, validUntil, photo, country)
}

@Serializable
data class DocumentDTO(
    val id: Int,
    val type: DocumentType,
    val number: String,
    val series: String,
    val issuedBy: String,
    @Contextual val issuedDate: LocalDate,
    @Contextual val validUntil: LocalDate,
    val photo: String,
    val country: String
)

enum class DocumentType {
    DOMESTIC_PASSPORT,
    FOREIGN_PASSPORT,
}

object Documents : IntIdTable() {
    val userInfoId = reference("user_info_id", UserInfos)
    val type = enumeration("type", DocumentType::class)
    val number = varchar("number", 128)
    val series = varchar("series", 128)
    val issuedBy = varchar("issued_by", 128)
    val issuedDate = date("issued_date")
    val validUntil = date("valid_until")
    val photo = varchar("photo", 128)
    val country = varchar("country", 128)
}