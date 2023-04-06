package ru.sejapoe.application.user

import org.jetbrains.exposed.sql.transactions.transaction
import ru.sejapoe.application.utils.*
import java.math.BigInteger
import java.nio.charset.StandardCharsets

private val activeRegistrations = mutableMapOf<String, BitArray256>()
private val activeLogins = mutableMapOf<String, Pair<User, BitArray256>>()

fun startRegistration(clientRegister: Pair<String, String>): BitArray256 {
    val login: String = clientRegister.first
    transaction {
        if (!User.find { Users.username eq login }.empty()) throw UserAlreadyExists()
    }
    val saltC: BitArray256 = BitArray256.fromBase64(clientRegister.second)
    val saltS: BitArray256 = random256()
    val salt: BitArray256 = xorByteArrays(saltS, saltC)
    activeRegistrations[login] = salt
    return saltS
}

fun finishRegistration(clientRegister: Pair<String, BigInteger>) {
    val login: String = clientRegister.first
    val salt = activeRegistrations[login]
    transaction {
        User.new {
            username = login
            this.salt = salt!!
            verifier = clientRegister.second
        }
    }
    activeRegistrations.remove(login)
}

fun login(clientLogin: Pair<String, BigInteger>): Pair<String, BigInteger> {
    val user: User = transaction {
        val find = User.find { Users.username eq clientLogin.first }
        find.firstOrNull()
    } ?: throw NoSuchUser()
    val b = random256()
    val bigInteger: BigInteger = k.multiply(user.verifier).add(modPow(b))
    val u: BitArray256 = hash(concatByteArrays(clientLogin.second.toByteArray(), bigInteger.toByteArray()))
    val s: BigInteger =
        clientLogin.second.multiply(user.verifier.modPow(u.asBigInteger(), N)).modPow(b.asBigInteger(), N)
    val sessionKey: BitArray256 = hash(s.toByteArray())
    val m: BitArray256 = hash(
        concatByteArrays(
            xorByteArrays(BitArray256.fromBigInteger(N), BitArray256.fromBigInteger(g)).asByteArray(),
            user.username.toByteArray(StandardCharsets.UTF_8),
            user.salt.asByteArray(),
            clientLogin.second.toByteArray(),
            bigInteger.toByteArray(),
            sessionKey.asByteArray()
        )
    )
    activeLogins[m.asBase64()] = user to sessionKey
    return user.salt.asBase64() to bigInteger
}

fun confirm(data: String): Int {
    val pair = activeLogins[data] ?: throw WrongPasswordException()
    return transaction {
        Session.new {
            user = pair.first
            sessionKey = pair.second
        }.id.value
    }
}

class UserAlreadyExists : Exception()

class NoSuchUser : Exception()


class WrongPasswordException : Exception()

