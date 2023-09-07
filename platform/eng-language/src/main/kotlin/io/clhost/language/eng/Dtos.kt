package io.clhost.language.eng

import kotlinx.serialization.Serializable

// In order to support serialization for native cli tools written in Kotlin, so
// I have to support kotlinx-serialization module in here

@Serializable
data class WordDefinitionDto(
    val word: String,
    val meanings: MutableSet<Meaning> = mutableSetOf(),
    val translations: MutableSet<Translation> = mutableSetOf(),
    val pronunciations: MutableSet<Pronunciation> = mutableSetOf(),
    val tags: MutableSet<String> = mutableSetOf(),
    val synonyms: MutableSet<String> = mutableSetOf()
)

@Serializable
data class PhraseDefinitionDto(
    val phrase: String,
    val meanings: MutableSet<Meaning> = mutableSetOf(),
    val translations: MutableSet<Translation> = mutableSetOf(),
    val tags: MutableSet<String> = mutableSetOf()
)
