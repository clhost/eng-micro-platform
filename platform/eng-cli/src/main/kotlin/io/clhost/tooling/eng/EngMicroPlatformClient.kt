package io.clhost.tooling.eng

import io.clhost.language.eng.CreatePhraseDefinitionCommand
import io.clhost.language.eng.CreateWordDefinitionCommand
import io.clhost.language.eng.PhraseDefinitionDto
import io.clhost.language.eng.WordDefinitionDto
import io.clhost.tooling.extensions.body
import io.clhost.tooling.extensions.encoded
import io.clhost.tooling.extensions.getAsync
import io.clhost.tooling.extensions.httpClient
import io.clhost.tooling.extensions.postAsync
import io.clhost.tooling.extensions.url

val engMicroPlatformClient = EngMicroPlatformClient()

class EngMicroPlatformClient {

    private val engMicroPlatformUrl: String by lazy { config.engMicroPlatformUrl }

    fun getWord(word: String) = httpClient.getAsync<WordDefinitionDto> {
        url("$engMicroPlatformUrl/word/${word.encoded}")
    }

    fun createWord(word: String) = httpClient.postAsync<WordDefinitionDto> {
        url("$engMicroPlatformUrl/word/${word.encoded}/create")
        body(CreateWordDefinitionCommand(word))
    }

    fun deleteWord(word: String) = httpClient.postAsync<Any> {
        url("$engMicroPlatformUrl/word/${word.encoded}/delete")
    }

    fun getPhrase(phrase: String) = httpClient.getAsync<PhraseDefinitionDto> {
        url("$engMicroPlatformUrl/phrase/${phrase.encoded}")
    }

    fun createPhrase(phrase: String) = httpClient.postAsync<PhraseDefinitionDto> {
        url("$engMicroPlatformUrl/phrase/${phrase.encoded}/create")
        body(CreatePhraseDefinitionCommand(phrase))
    }

    fun deletePhrase(phrase: String) = httpClient.postAsync<Any> {
        url("$engMicroPlatformUrl/phrase/${phrase.encoded}/delete")
    }
}
