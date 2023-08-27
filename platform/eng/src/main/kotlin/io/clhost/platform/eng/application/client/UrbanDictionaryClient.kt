package io.clhost.platform.eng.application.client

import com.fasterxml.jackson.annotation.JsonProperty
import io.clhost.extension.ktor.client.plugin.label
import io.clhost.platform.eng.application.WithSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.time.OffsetDateTime
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

    suspend fun getDefinitions(word: String): List<UrbanDictionaryDefinition> {
        var page = 1
        val definitions = mutableListOf<UrbanDictionaryDefinition>()

        do {
            val definitionsOnPage = getDefinitionsOnPage(word, page++)
            definitions.addAll(definitionsOnPage)
        } while (definitionsOnPage.isNotEmpty())

        return definitions
    }

    private suspend fun getDefinitionsOnPage(word: String, page: Int) =
        client.get("$url/v0/define?term=$word&page=$page") {
            label("getDefinition")
        }.body<UrbanDictionaryDefinitions>().list
}

internal data class UrbanDictionaryDefinitions(
    val list: List<UrbanDictionaryDefinition>
)

data class UrbanDictionaryDefinition(
    val definition: String,
    val example: String,

    @field:JsonProperty("thumbs_up")
    val thumbsUp: Int,

    @field:JsonProperty("thumbs_down")
    val thumbsDown: Int,

    @field:JsonProperty("written_on")
    val writtenOn: OffsetDateTime,

    override val source: String = "urbandictionary.com"
) : WithSource
