package ru.sejapoe.application.utils

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigInteger
import java.util.*

@Serializable
data class BitArray256(
    val bytes: ByteArray
) {
    fun asByteArray(): ByteArray {
        return bytes
    }

    fun asBigInteger(): BigInteger {
        val result = StringBuilder()
        for (b in bytes) {
            result.append(Integer.toHexString(b.toInt()))
        }
        return BigInteger(result.toString(), 16)
    }

    fun asBase64(): String {
        return Base64.getEncoder().encodeToString(bytes)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BitArray256

        return bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }

    companion object {
        fun fromBigInteger(source: BigInteger): BitArray256 {
            return fromByteArray(source.toByteArray())
        }

        fun fromByteArray(source: ByteArray): BitArray256 {
            val bytes = ByteArray(32)
            if (source.isNotEmpty()) {
                System.arraycopy(source, 0, bytes, Integer.max(32 - source.size, 0), Integer.min(source.size, 32))
            }
            return BitArray256(bytes)
        }

        fun fromBase64(source: String?): BitArray256 {
            return fromByteArray(Base64.getDecoder().decode(source))
        }
    }
}
