package io.clhost.platform.eng.application.processor

import io.clhost.extension.ktor.client.runBlockingWithPreservedCorrelationId
import io.clhost.extension.stdlib.withResult
import io.clhost.language.eng.CreateWordDefinitionCommand
import io.clhost.language.eng.Meaning
import io.clhost.language.eng.Pronunciation
import io.clhost.language.eng.Translation
import io.clhost.language.eng.WordDefinitionCommand
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
        val urbanDictionaryDefinitions: List<UrbanDictionaryDefinition>,
        val yandexTranslation: Pair<String?, String?>
    )

    override fun isSuitable(command: WordDefinitionCommand) = command is CreateWordDefinitionCommand

    // todo: create in here a call to another module

    override fun process(command: WordDefinitionCommand): WordDefinition = transactionTemplate.execute {
        command as CreateWordDefinitionCommand

        val word = command.word

        withResult { wordDefinitionService.get(word) }.onSuccess { return@execute it }

        val wordDefinition = wordDefinitionService.create(word)

        val definitions = runBlockingWithPreservedCorrelationId {
            val result = withResult { dictionaryClient.getDefinitions(word) }

            result.onFailure {
                if (it is ClientRequestException && it.response.status == NotFound) {
                    throw IllegalStateException("Word $word is not existed!")
                }
            }

            val dictionarySynonyms = async { dictionaryClient.getSynonyms(word) }
            val urbanDictionaryDefinitions = async { urbanDictionaryClient.getDefinitions(word) }
            val yandexTranslation = async { yandexCloudTranslateClient.translateEnToRu(word) }

            Definitions(
                dictionaryDefinitions = result.getOrThrow(),
                dictionarySynonyms = dictionarySynonyms.await(),
                urbanDictionaryDefinitions = urbanDictionaryDefinitions.await(),
                yandexTranslation = with(yandexTranslation.await()) { this?.source to this?.text }
            )
        }

        return@execute wordDefinition.apply {
            appendDictionarySynonyms(definitions.dictionarySynonyms)
            appendYandexTranslation(definitions.yandexTranslation)
            appendDictionaryDefinitions(definitions.dictionaryDefinitions)
            appendUrbanDictionaryDefinitions(definitions.urbanDictionaryDefinitions)
        }
    }!!

    private fun WordDefinition.appendDictionaryDefinitions(definitions: List<DictionaryDefinition>) {
        val meanings = definitions.mapNotNull {
            it.definition?.let { definition -> Meaning(it.source, definition, it.partOfSpeech) }
        }

        val pronunciations = definitions.mapNotNull {
            it.ipa?.let { ipa -> Pronunciation(it.source, ipa, it.pronunciationUrl) }
        }

        wordDefinitionService.appendMeanings(this, meanings)
        wordDefinitionService.appendPronunciations(this, pronunciations)
    }

    private fun WordDefinition.appendUrbanDictionaryDefinitions(definitions: List<UrbanDictionaryDefinition>) {
        val topDefinitions = definitions
            .sortedByDescending { it.thumbsUp - it.thumbsDown }
            .take(urbanDictionaryDefinitionsCount)

        val meanings = topDefinitions.map { Meaning(it.source, it.definition, example = it.example) }

        wordDefinitionService.appendMeanings(this, meanings)
    }

    private fun WordDefinition.appendDictionarySynonyms(synonyms: List<String>) {
        wordDefinitionService.appendSynonyms(this, synonyms)
    }

    private fun WordDefinition.appendYandexTranslation(translationWithSource: Pair<String?, String?>) {
        val source = translationWithSource.first ?: return
        val translation = translationWithSource.second ?: return
        wordDefinitionService.appendTranslations(this, listOf(Translation(source, translation, "ru")))
    }
}
