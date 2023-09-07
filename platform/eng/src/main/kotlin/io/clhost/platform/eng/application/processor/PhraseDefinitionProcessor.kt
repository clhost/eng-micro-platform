package io.clhost.platform.eng.application.processor

import io.clhost.language.eng.DefinitionCommand
import io.clhost.platform.eng.domain.PhraseDefinition

interface PhraseDefinitionProcessor {
    fun process(command: DefinitionCommand): PhraseDefinition
    fun isSuitable(command: DefinitionCommand): Boolean
}
