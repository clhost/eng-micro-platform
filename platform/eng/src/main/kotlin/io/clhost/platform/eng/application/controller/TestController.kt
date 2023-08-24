package io.clhost.platform.eng.application.controller

import io.clhost.platform.eng.application.client.UrbanDictionaryDefinition
import io.clhost.platform.eng.application.client.DictionaryClient
import io.clhost.platform.eng.application.client.DictionaryDefinition
import io.clhost.platform.eng.application.client.UrbanDictionaryClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(
    private val dictionaryClient: DictionaryClient,
    private val urbanDictionaryClient: UrbanDictionaryClient
) {

    @GetMapping("/1/word/{word}")
    fun urbanDictionary(@PathVariable word: String): List<UrbanDictionaryDefinition> {
        return urbanDictionaryClient.getDefinitions(word)
    }

    @GetMapping("/2/word/{word}")
    fun dictionary(@PathVariable word: String): List<DictionaryDefinition> {
        return dictionaryClient.getDefinitions(word)
    }
}
