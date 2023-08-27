package io.clhost.tooling.eng

import io.clhost.tooling.extensions.OffsetDateTimeSerializer
import io.clhost.tooling.extensions.body
import io.clhost.tooling.extensions.get
import io.clhost.tooling.extensions.getAsync
import io.clhost.tooling.extensions.httpClient
import io.clhost.tooling.extensions.postAsync
import io.clhost.tooling.extensions.url
import java.time.OffsetDateTime
import kotlinx.serialization.Serializable

val engMicroPlatformClient = EngMicroPlatformClient()

class EngMicroPlatformClient {

    // private val engMicroPlatformUrl: String by lazy { config.engMicroPlatformUrl }
    private val engMicroPlatformUrl = "http://localhost:11111"

    fun getWord(word: String) = httpClient.getAsync<WordDefinition>() {
        url("$engMicroPlatformUrl/word/$word")
    }

    fun createWord(word: String) = httpClient.postAsync<WordDefinition>() {
        url("$engMicroPlatformUrl/word/$word/create")
        body(CreateWordDefinition(word))
    }
}

// TODO: should extract eng-micro-platform-language

@Serializable
data class CreateWordDefinition(
    val word: String
)

@Serializable
class WordDefinition(
    val word: String,
    @Serializable(OffsetDateTimeSerializer::class)
    val createdAt: OffsetDateTime,
    @Serializable(OffsetDateTimeSerializer::class)
    val updatedAt: OffsetDateTime,
    val examples: MutableSet<Example> = mutableSetOf(),
    val meanings: MutableSet<Meaning> = mutableSetOf(),
    val translations: MutableSet<Translation> = mutableSetOf(),
    val pronunciations: MutableSet<Pronunciation> = mutableSetOf(),
    val tags: MutableSet<String> = mutableSetOf(),
    val synonyms: MutableSet<String> = mutableSetOf()
)

@Serializable
data class Example(
    val source: String,
    val definition: String
)

@Serializable
data class Translation(
    val source: String,
    val word: String,
    val language: String
)

@Serializable
data class Meaning(
    val source: String,
    val description: String,
    val partOfSpeech: String? = null
)

@Serializable
data class Pronunciation(
    val source: String,
    val ipa: String,
    val audioUrl: String? = null
)
