package io.clhost.platform.eng.application.processor

import io.clhost.extension.ktor.client.runBlockingWithPreservedCorrelationId
import io.clhost.extension.stdlib.withResult
import io.clhost.language.eng.CreateWordDefinitionCommand
import io.clhost.language.eng.Meaning
import io.clhost.language.eng.Pronunciation
import io.clhost.language.eng.Translation
import io.clhost.language.eng.DefinitionCommand
import io.clhost.platform.eng.application.client.DictionaryClient
import io.clhost.platform.eng.application.client.DictionaryDefinition
import io.clhost.platform.eng.application.client.UrbanDictionaryClient
import io.clhost.platform.eng.application.client.UrbanDictionaryDefinition
import io.clhost.platform.eng.application.client.YandexCloudTranslateClient
import io.clhost.platform.eng.domain.WordDefinition
import io.clhost.platform.eng.domain.WordDefinitionService
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode.Companion.NotFound
import kotlinx.coroutines.async
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate

@Component
class WordDefinitionCreator(
    private val transactionTemplate: TransactionTemplate,
    private val wordDefinitionService: WordDefinitionService,
    private val dictionaryClient: DictionaryClient,
    private val urbanDictionaryClient: UrbanDictionaryClient,
    private val yandexCloudTranslateClient: YandexCloudTranslateClient
) : WordDefinitionProcessor {

    companion object {
        const val urbanDictionaryDefinitionsCount = 3
    }

    private data class Definitions(
        val dictionaryDefinitions: List<DictionaryDefinition>,
        val dictionarySynonyms: List<String>,
        val yandexTranslation: Pair<String?, String?>,
        val urbanDictionaryDefinitions: List<UrbanDictionaryDefinition>
    )

    override fun isSuitable(command: DefinitionCommand) = command is CreateWordDefinitionCommand

    override fun process(command: DefinitionCommand): WordDefinition {
        command as CreateWordDefinitionCommand

        val word = command.word

        withResult { wordDefinitionService.get(word) }.onSuccess { return it }

        val definitions = runBlockingWithPreservedCorrelationId {
            val dictionaryResult = withResult { dictionaryClient.getDefinitions(word) }

            dictionaryResult.onFailure {
                if (it is ClientRequestException && it.response.status == NotFound) {
                    throw IllegalStateException("Word $word is not existed!")
                }
            }

            val dictionarySynonyms = async { dictionaryClient.getSynonyms(word) }
            val urbanDictionaryDefinitions = async { urbanDictionaryClient.getDefinitions(word) }
            val yandexTranslation = async { yandexCloudTranslateClient.translateEnToRu(word) }

            val urbanDictionaryResult = withResult { urbanDictionaryDefinitions.await() }

            Definitions(
                dictionaryDefinitions = dictionaryResult.getOrThrow(),
                dictionarySynonyms = dictionarySynonyms.await(),
                yandexTranslation = with(yandexTranslation.await()) { this?.source to this?.text },
                urbanDictionaryDefinitions = urbanDictionaryResult.getOrDefault(listOf())
            )
        }

        return transactionTemplate.execute {
            wordDefinitionService.create(word).apply {
                wordDefinitionService.appendDictionarySynonyms(this, definitions.dictionarySynonyms)
                wordDefinitionService.appendYandexTranslation(this, definitions.yandexTranslation)
                wordDefinitionService.appendDictionaryDefinitions(this, definitions.dictionaryDefinitions)
                wordDefinitionService.appendUrbanDictionaryDefinitions(this, definitions.urbanDictionaryDefinitions)
            }
        }!!
    }

    private fun WordDefinitionService.appendDictionaryDefinitions(
        wordDefinition: WordDefinition,
        definitions: List<DictionaryDefinition>
    ) {
        val meanings = definitions.mapNotNull {
            it.definition?.let { definition -> Meaning(it.source, definition, it.partOfSpeech) }
        }

        val pronunciations = definitions.mapNotNull {
            it.ipa?.let { ipa -> Pronunciation(it.source, ipa, it.pronunciationUrl) }
        }

        appendMeanings(wordDefinition, meanings)
        appendPronunciations(wordDefinition, pronunciations)
    }

    private fun WordDefinitionService.appendUrbanDictionaryDefinitions(
        wordDefinition: WordDefinition,
        definitions: List<UrbanDictionaryDefinition>
    ) {
        val topDefinitions =
            definitions.sortedByDescending { it.thumbsUp - it.thumbsDown }.take(urbanDictionaryDefinitionsCount)

        val meanings = topDefinitions.map { Meaning(it.source, it.definition, example = it.example) }
        appendMeanings(wordDefinition, meanings)
    }

    private fun WordDefinitionService.appendDictionarySynonyms(wordDefinition: WordDefinition, synonyms: List<String>) {
        appendSynonyms(wordDefinition, synonyms)
    }

    private fun WordDefinitionService.appendYandexTranslation(
        wordDefinition: WordDefinition,
        translationWithSource: Pair<String?, String?>
    ) {
        val source = translationWithSource.first ?: return
        val translation = translationWithSource.second ?: return
        appendTranslations(wordDefinition, listOf(Translation(source, translation, "ru")))
    }
}
