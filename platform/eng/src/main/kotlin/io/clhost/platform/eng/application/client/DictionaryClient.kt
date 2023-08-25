package io.clhost.platform.eng.application.client

import io.clhost.extension.html.extractTextFromHtml
import io.clhost.extension.jackson.jsonDecode
import io.clhost.extension.ktor.client.plugin.label
import io.clhost.extension.ktor.client.runBlockingWithPreservedCorrelationId
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DictionaryClient(
    @Value("\${client.dictionary.direct-url}")
    private val directUrl: String,

    @Value("\${client.dictionary.api-url}")
    private val apiUrl: String,

    @Qualifier("dictionaryKtorClient")
    private val client: HttpClient
) {

    companion object {
        const val replacementKey = "window.__PRELOADED_STATE__ = "
    }

    fun getDefinitions(word: String) = runBlockingWithPreservedCorrelationId {
        val html = getRawHtmlPage(word)

        val json = extractTextFromHtml(html, replacementKey)
            ?: throw Exception("Can't extract json block of definition")

        val block = jsonDecode<DictionaryApiBlock>(json)

        val ipa = block.ipa()
        val pronunciationUrl = block.pronunciationUrl()

        val definitions = block.shortDefinition()

        definitions.map {
            DictionaryDefinition(
                definition = extractTextFromHtml(it.definition),
                ipa = ipa,
                partOfSpeech = extractTextFromHtml(it.partOfSpeech),
                pronunciationUrl = pronunciationUrl
            )
        }
    }

    fun getSynonyms(word: String) = runBlockingWithPreservedCorrelationId {
        client.get("$apiUrl/synonyms/$word") {
            label("getSynonyms")
        }.body<DictionarySynonymsBlock>().data.synonyms.map { it.term }
    }

    private suspend fun getRawHtmlPage(word: String) =
        client.get("$directUrl/browse/$word") { label("getRawHtmlPage") }.body<String>()
}

data class DictionaryDefinition(
    val definition: String? = null,
    val ipa: String? = null,
    val partOfSpeech: String? = null,
    val pronunciationUrl: String? = null
)
