package io.clhost.platform.eng.application.client

import io.clhost.extension.ktor.client.plugin.label
import io.clhost.extension.stdlib.encoded
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class UrbanDictionaryClient(
    @Value("\${client.urban-dictionary.url}")
    private val url: String,

    @Qualifier("urbanDictionaryKtorClient")
    private val client: HttpClient
) {

    suspend fun getDefinitions(wordOrPhrase: String): List<UrbanDictionaryDefinition> {
        var page = 1
        val definitions = mutableListOf<UrbanDictionaryDefinition>()

        do {
            val definitionsOnPage = getDefinitionsOnPage(wordOrPhrase, page++)
            definitions.addAll(definitionsOnPage)
        } while (definitionsOnPage.isNotEmpty())

        return definitions
    }

    private suspend fun getDefinitionsOnPage(wordOrPhrase: String, page: Int) =
        client.get("$url/v0/define?term=${wordOrPhrase.encoded}&page=$page") {
            label("getDefinition")
        }.body<UrbanDictionaryDefinitions>().list
}

internal data class UrbanDictionaryDefinitions(
    val list: List<UrbanDictionaryDefinition>
)
