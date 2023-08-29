package io.clhost.language.eng

import kotlinx.serialization.Serializable

// In order to support serialization for native cli tools written in Kotlin, so
// I have to support kotlinx-serialization module in here

sealed interface WordDefinitionCommand

@Serializable
data class CreateWordDefinitionCommand(
    val word: String
) : WordDefinitionCommand
