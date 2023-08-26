package io.clhost.platform.eng.application.commands

sealed interface WordDefinitionCommand

data class CreateWordDefinition(
    val word: String
) : WordDefinitionCommand
