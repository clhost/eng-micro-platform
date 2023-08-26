package io.clhost.platform.eng.application.controller

import io.clhost.platform.eng.application.commands.CreateWordDefinition
import io.clhost.platform.eng.application.processor.WordDefinitionProcessor
import io.clhost.platform.eng.domain.WordDefinition
import io.clhost.platform.eng.domain.WordDefinitionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WordDefinitionController(
    private val processors: List<WordDefinitionProcessor>,
    private val wordDefinitionService: WordDefinitionService
) {

    @GetMapping("/wordDefinition/{word}")
    fun getWordDefinition(@PathVariable word: String): WordDefinition {
        return wordDefinitionService.get(word)
    }

    @PostMapping("/word/{word}/create")
    fun createWordDefinition(@PathVariable word: String): WordDefinition {
        val command = CreateWordDefinition(word)
        return processors.single { it.isSuitable(command) }.process(command)
    }
}
