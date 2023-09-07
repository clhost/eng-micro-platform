package io.clhost.language.eng

import kotlinx.serialization.Serializable

// In order to support serialization for native cli tools written in Kotlin, so
// I have to support kotlinx-serialization module in here

sealed interface DefinitionCommand {
    val comment: String?
}

@Serializable
data class CreateWordDefinitionCommand(
    val word: String,
    override val comment: String? = null
) : DefinitionCommand

@Serializable
data class CreatePhraseDefinitionCommand(
    val phrase: String,
    override val comment: String? = null
) : DefinitionCommand
