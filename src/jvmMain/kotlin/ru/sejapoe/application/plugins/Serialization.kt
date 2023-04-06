package ru.sejapoe.application.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.PairSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.math.BigInteger

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(json = Json {
            serializersModule = SerializersModule {
                contextual(BigIntegerSerializer)
//                contextual(PairSerializer(String.serializer(), BigIntegerSerializer))
            }
        })
    }
}

//@Serializer(forClass = BigInteger::class)
object BigIntegerSerializer: KSerializer<BigInteger> {
    override fun deserialize(decoder: Decoder) = decoder.decodeString().toBigInteger()

    override fun serialize(encoder: Encoder, value: BigInteger) = encoder.encodeString(value.toString())

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("BigInteger", PrimitiveKind.STRING)
}