package io.clhost.tooling.extensions

import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

// We have to enrich this module each time when new @Serializable class has been introduced
// because of native-build with reflection limitations.

var module: () -> SerializersModule = { SerializersModule {} }

val json: Json
    get() = Json { ignoreUnknownKeys = true; serializersModule = module() }

val yaml: Yaml
    get() = Yaml(module(), YamlConfiguration(encodingIndentationSize = 4, sequenceStyle = SequenceStyle.Flow))

inline fun <reified T> String.jsonDecoded() = json.decodeFromString<T>(this)

inline fun <reified T> T.jsonEncoded() = json.encodeToString(this)

inline fun <reified T> String.yamlDecoded() = yaml.decodeFromString<T>(this)

inline fun <reified T> T.yamlEncoded() = yaml.encodeToString(this)

object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "OffsetDateTime",
        kind = PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        val string = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value)
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        val string = decoder.decodeString()
        return OffsetDateTime.parse(string)
    }
}
