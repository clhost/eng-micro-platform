package io.clhost.platform.eng.application.controller

import io.clhost.language.eng.CreatePhraseDefinitionCommand
import io.clhost.platform.eng.application.processor.PhraseDefinitionProcessor
import io.clhost.platform.eng.domain.PhraseDefinition
import io.clhost.platform.eng.domain.PhraseDefinitionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PhraseDefinitionController(
    private val processors: List<PhraseDefinitionProcessor>,
    private val phraseDefinitionService: PhraseDefinitionService
) {

    @GetMapping("/phrase/{phrase}")
    fun getPhrase(@PathVariable phrase: String): PhraseDefinition {
        return phraseDefinitionService.get(phrase)
    }

    @PostMapping("/phrase/{phrase}/create")
    fun createPhrase(@PathVariable phrase: String): PhraseDefinition {
        val command = CreatePhraseDefinitionCommand(phrase)
        return processors.single { it.isSuitable(command) }.process(command)
    }

    @PostMapping("/phrase/{phrase}/delete")
    fun deletePhrase(@PathVariable phrase: String) {
        phraseDefinitionService.delete(phrase)
    }
}
