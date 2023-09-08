package io.clhost.platform.eng.application.client

import io.clhost.extension.ktor.client.applicationJson
import io.clhost.extension.stdlib.encoded
import io.clhost.extension.stdlib.ifExceptionCaught
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MerriamWebsterClient(
    @Value("\${client.merriam-webster.url}")
    private val url: String,

    @Value("\${client.merriam-webster.api-key}")
    private val apiKey: String,

    @Qualifier("merriamWebsterKtorClient")
    private val client: HttpClient
) {

    suspend fun getDefinitions(wordOrPhrase: String): MerriamWebsterDefinition? {
        val definition = ifExceptionCaught<Exception, List<MerriamWebsterResponse>>(listOf()) {
            client.get("$url/${wordOrPhrase.encoded}") {
                applicationJson()
                parameter("key", apiKey)
            }.body<List<MerriamWebsterResponse>>()
        }.firstOrNull { it.meta.id == wordOrPhrase } ?: return null
        return MerriamWebsterDefinition(definitions = definition.shortdef, partOfSpeech = definition.fl)
    }
}

data class MerriamWebsterResponse(
    val meta: Meta,
    val fl: String? = null,
    val shortdef: List<String> = listOf()
) {
    data class Meta(
        val id: String
    )
}
