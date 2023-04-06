package ru.sejapoe.application.utils

import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.experimental.xor

val g = BigInteger.valueOf(2)
val N = BigInteger("EEAF0AB9ADB38DD69C33F80AA8FC5E86072618775FF3C0B9EA2314C9C256576D674DF7", 16)
val k: BigInteger = hash(g.toByteArray(), N.toByteArray()).asBigInteger()

fun modPow(bitArray256: BitArray256): BigInteger {
    return g.modPow(bitArray256.asBigInteger(), N)
}

fun random256(): BitArray256 {
    val salt = ByteArray(32)
    val secureRandom = SecureRandom()
    secureRandom.nextBytes(salt)
    return BitArray256.fromByteArray(salt)
}

fun concatByteArrays(vararg arr: ByteArray): ByteArray {
    val outputStream = ByteArrayOutputStream()
    for (bytes in arr) {
        outputStream.write(bytes)
    }
    return outputStream.toByteArray()
}

fun xorByteArrays(a: BitArray256, b: BitArray256): BitArray256 {
    val res = ByteArray(32)
    for (i in 0..31) {
        res[i] = (a.asByteArray()[i] xor b.asByteArray()[i])
    }
    return BitArray256.fromByteArray(res)
}

fun hash(bytes: ByteArray?): BitArray256 {
    return BitArray256.fromByteArray(MessageDigest.getInstance("SHA-256").digest(bytes))
}

fun hash(bytes: ByteArray?, salt: ByteArray?): BitArray256 {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(salt)
    return BitArray256.fromByteArray(messageDigest.digest(bytes))
}