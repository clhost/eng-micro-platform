package io.clhost.platform.eng.application.processor

import io.clhost.language.eng.DefinitionCommand
import io.clhost.platform.eng.domain.WordDefinition

interface WordDefinitionProcessor {
    fun process(command: DefinitionCommand): WordDefinition
    fun isSuitable(command: DefinitionCommand): Boolean
}
