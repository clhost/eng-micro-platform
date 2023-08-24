package io.clhost.platform.eng.application.client

import io.clhost.extension.ktor.client.plugin.label
import io.clhost.extension.ktor.client.runBlockingWithPreservedCorrelationId
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

    fun getDefinitions(word: String) = runBlockingWithPreservedCorrelationId {
        client.get("$url/v0/define?term=$word") {
            label("getDefinition")
        }.body<Definitions>().list
    }
}

data class Definitions(
    val list: List<Definition>
)

data class Definition(
    val author: String,
    val definition: String,
    val example: String,
    val thumbsUp: Int,
    val thumbsDown: Int,
    val writtenOn: OffsetDateTime
)
