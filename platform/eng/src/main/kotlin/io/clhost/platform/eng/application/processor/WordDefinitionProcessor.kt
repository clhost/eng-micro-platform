package io.clhost.platform.eng.application.processor

import io.clhost.platform.eng.application.commands.WordDefinitionCommand
import io.clhost.platform.eng.domain.WordDefinition

interface WordDefinitionProcessor {
    fun process(command: WordDefinitionCommand): WordDefinition
    fun isSuitable(command: WordDefinitionCommand): Boolean
}