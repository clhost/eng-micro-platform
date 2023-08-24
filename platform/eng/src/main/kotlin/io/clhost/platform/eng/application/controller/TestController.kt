package io.clhost.platform.eng.application.controller

import io.clhost.platform.eng.application.client.Definition
import io.clhost.platform.eng.application.client.UrbanDictionaryClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(
    private val urbanDictionaryClient: UrbanDictionaryClient
) {

    @GetMapping("/word/{word}")
    fun test(@PathVariable word: String): List<Definition> {
        return urbanDictionaryClient.getDefinitions(word)
    }
}
