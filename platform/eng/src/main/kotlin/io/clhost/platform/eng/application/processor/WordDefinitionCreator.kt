package io.clhost.platform.eng.application.processor

import io.clhost.extension.ktor.client.runBlockingWithPreservedCorrelationId
import io.clhost.platform.eng.application.client.DictionaryClient
import io.clhost.platform.eng.application.client.DictionaryDefinition
import io.clhost.platform.eng.application.client.UrbanDictionaryClient
import io.clhost.platform.eng.application.client.UrbanDictionaryDefinition
import io.clhost.platform.eng.application.client.YandexCloudTranslateClient
import io.clhost.platform.eng.application.commands.CreateWordDefinition
import io.clhost.platform.eng.application.commands.WordDefinitionCommand
import io.clhost.platform.eng.domain.Example
import io.clhost.platform.eng.domain.Meaning
import io.clhost.platform.eng.domain.Pronunciation
import io.clhost.platform.eng.domain.Translation
import io.clhost.platform.eng.domain.WordDefinition
import io.clhost.platform.eng.domain.WordDefinitionService
import kotlinx.coroutines.async
import org.springframework.stereotype.Component

@Component
class WordDefinitionCreator(
    private val wordDefinitionService: WordDefinitionService,
    private val dictionaryClient: DictionaryClient,
    private val urbanDictionaryClient: UrbanDictionaryClient,
    private val yandexCloudTranslateClient: YandexCloudTranslateClient
) : WordDefinitionProcessor {

    companion object {
        const val urbanDictionaryDefinitionsCount = 3
    }

    // todo: refactoring - should extract Source
    // todo: examples from dictionary.com
    // todo: partOfSpeech became nullable because urbandictionary doesn't define it
    // todo: Pronunciation#audioUrl became nullable
    // todo: yandex IAM token can be expired FUCK

    private data class Definitions(
        val dictionaryDefinitions: List<DictionaryDefinition>,
        val dictionarySynonyms: List<String>,
        val urbanDictionaryDefinitions: List<UrbanDictionaryDefinition>,
        val yandexTranslation: Pair<String, String?>
    )

    override fun isSuitable(command: WordDefinitionCommand) = command is CreateWordDefinition

    override fun process(command: WordDefinitionCommand): WordDefinition {
        command as CreateWordDefinition

        val word = command.word
        val wordDefinition = wordDefinitionService.create(word)

        val definitions = runBlockingWithPreservedCorrelationId {
            val dictionaryDefinitions = async { dictionaryClient.getDefinitions(word) }
            val dictionarySynonyms = async { dictionaryClient.getSynonyms(word) }
            val urbanDictionaryDefinitions = async { urbanDictionaryClient.getDefinitions(word) }
            val yandexTranslation = async { yandexCloudTranslateClient.translateEnToRu(word) }
            Definitions(
                dictionaryDefinitions = dictionaryDefinitions.await(),
                dictionarySynonyms = dictionarySynonyms.await(),
                urbanDictionaryDefinitions = urbanDictionaryDefinitions.await(),
                yandexTranslation = "yandex" to yandexTranslation.await()
            )
        }

        return wordDefinition.apply {
            appendDictionarySynonyms(definitions.dictionarySynonyms)
            appendYandexTranslation(definitions.yandexTranslation)
            appendDictionaryDefinitions(definitions.dictionaryDefinitions)
            appendUrbanDictionaryDefinitions(definitions.urbanDictionaryDefinitions)
        }
    }

    private fun WordDefinition.appendDictionaryDefinitions(definitions: List<DictionaryDefinition>) {
        val meanings = definitions.mapNotNull {
            it.definition?.let { definition -> Meaning("dictionary.com", definition, it.partOfSpeech) }
        }

        val pronunciations = definitions.mapNotNull {
            it.ipa?.let { ipa -> Pronunciation("dictionary.com", ipa, it.pronunciationUrl) }
        }

        wordDefinitionService.appendMeanings(this, meanings)
        wordDefinitionService.appendPronunciations(this, pronunciations)
    }

    private fun WordDefinition.appendUrbanDictionaryDefinitions(definitions: List<UrbanDictionaryDefinition>) {
        val topDefinitions = definitions
            .sortedByDescending { it.thumbsUp - it.thumbsDown }
            .take(urbanDictionaryDefinitionsCount)

        val examples = topDefinitions.map { Example("urbandictionary.com", it.example) }
        val meanings = topDefinitions.map { Meaning("urbandictionary.com", it.definition) }

        wordDefinitionService.appendExamples(this, examples)
        wordDefinitionService.appendMeanings(this, meanings)
    }

    private fun WordDefinition.appendDictionarySynonyms(synonyms: List<String>) {
        wordDefinitionService.appendSynonyms(this, synonyms)
    }

    private fun WordDefinition.appendYandexTranslation(translationWithSource: Pair<String, String?>) {
        val source = translationWithSource.first
        val translation = translationWithSource.second ?: return
        wordDefinitionService.appendTranslations(this, listOf(Translation(source, translation, "ru")))
    }
}
