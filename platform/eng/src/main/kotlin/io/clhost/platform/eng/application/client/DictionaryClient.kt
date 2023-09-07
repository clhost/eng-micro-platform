package io.clhost.platform.eng.application.client

import com.fasterxml.jackson.annotation.JsonProperty
import io.clhost.extension.html.extractTextFromHtml
import io.clhost.extension.jackson.jsonDecode
import io.clhost.extension.ktor.client.plugin.label
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

    suspend fun getDefinitions(word: String): List<DictionaryDefinition> {
        val html = getRawHtmlPage(word)

        val json = extractTextFromHtml(html, replacementKey)
            ?: throw Exception("Can't extract json block of definition")

        val block = jsonDecode<DictionaryApiBlock>(json)

        val ipa = block.ipa()
        val pronunciationUrl = block.pronunciationUrl()

        val definitions = block.shortDefinition()

        return definitions.map {
            DictionaryDefinition(
                definition = extractTextFromHtml(it.definition),
                ipa = ipa,
                partOfSpeech = extractTextFromHtml(it.partOfSpeech),
                pronunciationUrl = pronunciationUrl
            )
        }
    }

    suspend fun getSynonyms(word: String) =
        client.get("$apiUrl/synonyms/$word") {
            label("getSynonyms")
        }.body<DictionarySynonymsBlock>().data.synonyms.map { it.term }

    private suspend fun getRawHtmlPage(word: String) =
        client.get("$directUrl/browse/$word") {
            label("getRawHtmlPage")
        }.body<String>()
}

internal fun DictionaryApiBlock.pronunciationUrl(): String? {
    return luna.resultsData.data.content
        .flatMap { it.entries }
        .mapNotNull { it.pronunciation }
        .firstNotNullOfOrNull { if (!it.audio?.url.isNullOrBlank()) it.audio?.url else null }
}

internal fun DictionaryApiBlock.ipa(): String? {
    return luna.resultsData.data.content
        .flatMap { it.entries }
        .mapNotNull { it.pronunciation }
        .firstNotNullOfOrNull { if (!it.ipa.isNullOrBlank()) it.ipa else null }
}

internal fun DictionaryApiBlock.shortDefinition(): List<ShortDefinition> {
    return luna.resultsData.data.content
        .flatMap { it.entries }
        .flatMap { it.posBlocks }
        .flatMap { d ->
            d.definitions.map {
                ShortDefinition(
                    partOfSpeech = d.pos.replace(",", ""),
                    definition = it.definition?.replace("\\s+".toRegex(), " ")
                )
            }
        }
}

internal data class ShortDefinition(
    val partOfSpeech: String? = null,
    val definition: String? = null
)

internal data class DictionarySynonymsBlock(
    val data: DictionarySynonymsData
) {
    data class DictionarySynonymsData(
        val synonyms: List<DictionarySynonym>
    )

    data class DictionarySynonym(
        val term: String
    )
}

internal data class DictionaryApiBlock(
    val luna: DictionaryLuna
) {
    data class DictionaryLuna(
        val resultsData: DictionaryResultsData
    )

    data class DictionaryResultsData(
        val data: DictionaryData
    )

    data class DictionaryData(
        val content: List<DictionaryContent> = listOf()
    )

    data class DictionaryContent(
        val source: String,
        val entries: List<DictionaryEntry> = listOf()
    )

    data class DictionaryEntry(
        val pronunciation: DictionaryPronunciation? = null,
        val posBlocks: List<DictionaryPosBlock> = listOf()
    )

    data class DictionaryPronunciation(
        val ipa: String? = null,
        val audio: DictionaryAudio? = null
    )

    data class DictionaryAudio(
        @field:JsonProperty("audio/mpeg")
        val url: String? = null
    )

    data class DictionaryPosBlock(
        val pos: String,
        val posSupplementaryInfo: String? = null,
        val definitions: List<DictionaryPosBlockDefinition> = listOf()
    )

    data class DictionaryPosBlockDefinition(
        val definition: String? = null
    )
}
