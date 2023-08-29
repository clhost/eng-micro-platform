package io.clhost.language.eng

import kotlinx.serialization.Serializable

// In order to support serialization for native cli tools written in Kotlin, so
// I have to support kotlinx-serialization module in here

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
    val partOfSpeech: String? = null,
    val example: String? = null
)

@Serializable
data class Pronunciation(
    val source: String,
    val ipa: String,
    val audioUrl: String? = null
)
