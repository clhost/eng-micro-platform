package io.clhost.platform.eng.application.client

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

interface WithSource {
    val source: String
}

data class DictionaryDefinition(
    val definition: String? = null,
    val ipa: String? = null,
    val partOfSpeech: String? = null,
    val pronunciationUrl: String? = null,
    override val source: String = "dictionary"
) : WithSource

data class UrbanDictionaryDefinition(
    val definition: String,
    val example: String,

    @field:JsonProperty("thumbs_up")
    val thumbsUp: Int,

    @field:JsonProperty("thumbs_down")
    val thumbsDown: Int,

    @field:JsonProperty("written_on")
    val writtenOn: OffsetDateTime,

    override val source: String = "urban.dictionary"
) : WithSource

data class MerriamWebsterDefinition(
    val definitions: List<String>,
    val partOfSpeech: String? = null,
    override val source: String = "merriam.webster"
) : WithSource

data class YandexTranslation(
    val text: String,
    override val source: String = "cloud.yandex"
) : WithSource
