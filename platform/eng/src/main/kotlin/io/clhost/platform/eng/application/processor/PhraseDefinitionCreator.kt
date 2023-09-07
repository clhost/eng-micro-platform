package io.clhost.platform.eng.application.processor

import io.clhost.extension.ktor.client.runBlockingWithPreservedCorrelationId
import io.clhost.extension.stdlib.withResult
import io.clhost.language.eng.CreatePhraseDefinitionCommand
import io.clhost.language.eng.DefinitionCommand
import io.clhost.language.eng.Meaning
import io.clhost.language.eng.Translation
import io.clhost.platform.eng.application.client.MerriamWebsterClient
import io.clhost.platform.eng.application.client.MerriamWebsterDefinition
import io.clhost.platform.eng.application.client.UrbanDictionaryClient
import io.clhost.platform.eng.application.client.UrbanDictionaryDefinition
import io.clhost.platform.eng.application.client.YandexCloudTranslateClient
import io.clhost.platform.eng.domain.PhraseDefinition
import io.clhost.platform.eng.domain.PhraseDefinitionService
import kotlinx.coroutines.async
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate

@Component
class PhraseDefinitionCreator(
    private val transactionTemplate: TransactionTemplate,
    private val phraseDefinitionService: PhraseDefinitionService,
    private val urbanDictionaryClient: UrbanDictionaryClient,
    private val merriamWebsterClient: MerriamWebsterClient,
    private val yandexCloudTranslateClient: YandexCloudTranslateClient
) : PhraseDefinitionProcessor {

    companion object {
        const val urbanDictionaryDefinitionsCount = 3
    }

    private data class Definitions(
        val yandexTranslation: Pair<String?, String?>,
        val urbanDictionaryDefinitions: List<UrbanDictionaryDefinition>,
        val merriamWebsterDefinition: MerriamWebsterDefinition? = null
    )

    override fun isSuitable(command: DefinitionCommand) = command is CreatePhraseDefinitionCommand

    override fun process(command: DefinitionCommand): PhraseDefinition {
        command as CreatePhraseDefinitionCommand

        val phrase = command.phrase

        withResult { phraseDefinitionService.get(phrase) }.onSuccess { return it }

        val definitions = runBlockingWithPreservedCorrelationId {
            val merriamWebsterDefinition = async { merriamWebsterClient.getDefinitions(phrase) }
            val urbanDictionaryDefinitions = async { urbanDictionaryClient.getDefinitions(phrase) }
            val yandexTranslation = async { yandexCloudTranslateClient.translateEnToRu(phrase) }

            val merriamWebsterResult = withResult { merriamWebsterDefinition.await() }
            val urbanDictionaryResult = withResult { urbanDictionaryDefinitions.await() }

            Definitions(
                merriamWebsterDefinition = merriamWebsterResult.getOrNull(),
                urbanDictionaryDefinitions = urbanDictionaryResult.getOrDefault(listOf()),
                yandexTranslation = with(yandexTranslation.await()) { this?.source to this?.text }
            )
        }

        return transactionTemplate.execute {
            phraseDefinitionService.create(phrase).apply {
                phraseDefinitionService.appendUrbanDictionaryDefinitions(this, definitions.urbanDictionaryDefinitions)
                phraseDefinitionService.appendYandexTranslation(this, definitions.yandexTranslation)
                phraseDefinitionService.appendMerriamWebsterDefinition(this, definitions.merriamWebsterDefinition)
            }
        }!!
    }

    private fun PhraseDefinitionService.appendMerriamWebsterDefinition(
        phraseDefinition: PhraseDefinition,
        merriamWebsterDefinition: MerriamWebsterDefinition? = null
    ) {
        if (merriamWebsterDefinition == null) return

        val source = merriamWebsterDefinition.source
        val partOfSpeech = merriamWebsterDefinition.partOfSpeech

        val meanings = merriamWebsterDefinition.definitions.map { Meaning(source, it, partOfSpeech) }
        appendMeanings(phraseDefinition, meanings)
    }

    private fun PhraseDefinitionService.appendUrbanDictionaryDefinitions(
        phraseDefinition: PhraseDefinition,
        definitions: List<UrbanDictionaryDefinition>
    ) {
        val topDefinitions = definitions
            .sortedByDescending { it.thumbsUp - it.thumbsDown }
            .take(urbanDictionaryDefinitionsCount)

        val meanings = topDefinitions.map { Meaning(it.source, it.definition, example = it.example) }
        appendMeanings(phraseDefinition, meanings)
    }

    private fun PhraseDefinitionService.appendYandexTranslation(
        phraseDefinition: PhraseDefinition,
        translationWithSource: Pair<String?, String?>
    ) {
        val source = translationWithSource.first ?: return
        val translation = translationWithSource.second ?: return
        appendTranslations(phraseDefinition, listOf(Translation(source, translation, "ru")))
    }
}
