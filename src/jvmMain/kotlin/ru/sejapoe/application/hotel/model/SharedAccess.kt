package ru.sejapoe.application.hotel.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import ru.sejapoe.application.user.User
import ru.sejapoe.application.user.UserLessDTO
import ru.sejapoe.application.user.Users
import kotlin.experimental.and
import kotlin.experimental.or

class SharedAccess(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SharedAccess>(SharedAccesses)

    var occupation by Occupation referencedOn SharedAccesses.occupation
    var user by User referencedOn SharedAccesses.user
    var rights by SharedAccesses.rights.transform(RightsComposition::value, ::RightsComposition)
    var budget by SharedAccesses.budget
    fun asDTO() = SharedAccessDTO(id.value, rights.value, user.asLessDTO(), budget)
}

enum class Rights(val value: Short) {
    ACCESS_ROOM(1), // open and close doors
    MANAGE_ROOM(2), // set dnd
    MANAGE_RESERVATIONS(4),
    PAY_FOR_SERVICES(8),
}

data class RightsComposition(val value: Short) {
    fun satisfy(right: Rights) = (value and right.value) != 0.toShort()
    fun satisfyAll(vararg rights: Rights) = rights.all(::satisfy)

    companion object {
        val default = of(Rights.ACCESS_ROOM)
        fun of(vararg rights: Rights) = RightsComposition(rights.fold(0) { acc, right -> acc or right.value })
    }
}

@Serializable
data class SharedAccessDTO(val id: Int, val rightsValue: Short, val user: UserLessDTO, val budget: Int)

object SharedAccesses : IntIdTable() {
    val occupation = reference("occupation", Occupations)
    val user = reference("user", Users)
    val rights = short("rights").default(RightsComposition.default.value)
    val budget = integer("budget")
}