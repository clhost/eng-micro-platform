package io.clhost.platform.eng.application.client

import com.fasterxml.jackson.annotation.JsonProperty

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
                    partOfSpeech = d.pos,
                    definition = it.definition.replace("\\s+".toRegex(), " ")
                )
            }
        }
}

internal data class ShortDefinition(
    val partOfSpeech: String? = null,
    val definition: String? = null,
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
        val definition: String
    )
}
