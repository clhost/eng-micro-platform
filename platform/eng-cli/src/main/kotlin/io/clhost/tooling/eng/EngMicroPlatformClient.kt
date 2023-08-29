package io.clhost.tooling.eng

import io.clhost.language.eng.CreateWordDefinitionCommand
import io.clhost.language.eng.WordDefinitionDto
import io.clhost.tooling.extensions.body
import io.clhost.tooling.extensions.getAsync
import io.clhost.tooling.extensions.httpClient
import io.clhost.tooling.extensions.postAsync
import io.clhost.tooling.extensions.url

val engMicroPlatformClient = EngMicroPlatformClient()

class EngMicroPlatformClient {

    private val engMicroPlatformUrl: String by lazy { config.engMicroPlatformUrl }

    fun getWord(word: String) = httpClient.getAsync<WordDefinitionDto> {
        url("$engMicroPlatformUrl/word/$word")
    }

    fun createWord(word: String) = httpClient.postAsync<WordDefinitionDto> {
        url("$engMicroPlatformUrl/word/$word/create")
        body(CreateWordDefinitionCommand(word))
    }
}
