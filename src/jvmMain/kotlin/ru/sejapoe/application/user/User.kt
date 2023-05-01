package ru.sejapoe.application.user

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import ru.sejapoe.application.hotel.model.Booking
import ru.sejapoe.application.hotel.model.Bookings
import ru.sejapoe.application.hotel.model.Occupation
import ru.sejapoe.application.hotel.model.Occupations
import ru.sejapoe.application.utils.BitArray256

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var username by Users.username
    var salt by Users.salt.transform({ it.bytes }, { BitArray256(it) })
    var verifier by Users.verifier.transform({ it.toString() }, { it.toBigInteger() })
    val bookings by Booking referrersOn Bookings.guest
    val occupations by Occupation referrersOn Occupations.guest
    var userInfo by UserInfo optionalReferencedOn Users.userInfo
    val friends
        get() = Friendship.find {
            (Friendships.accepted eq true) and
                    ((Friendships.from eq id) or (Friendships.to eq id))
        }
    val outcomeFriendRequests
        get() = Friendship.find {
            (Friendships.accepted eq false) and
                    (Friendships.from eq id)
        }
    val incomeFriendRequests
        get() = Friendship.find {
            (Friendships.accepted eq false) and
                    (Friendships.to eq id)
        }

    val notificationTokens: List<String>
        get() = Session.find { Sessions.user eq id }.mapNotNull { it.notificationToken }

    fun asDTO() = UserDTO(id.value, username, userInfo?.asDTO())
    fun asLessDTO() = UserLessDTO(id.value, username, userInfo?.fullName)
}

@Serializable
data class UserDTO(val id: Int, val username: String, val userInfo: UserInfoDTO?)

@Serializable
data class UserLessDTO(val id: Int, val username: String, val fullName: String?)

object Users : IntIdTable() {
    val username = varchar("username", 128).uniqueIndex()
    val salt = binary("salt", 32)
    val verifier = varchar("verifier", 128)
    val userInfo = reference("user_info_id", UserInfos).nullable()
}
