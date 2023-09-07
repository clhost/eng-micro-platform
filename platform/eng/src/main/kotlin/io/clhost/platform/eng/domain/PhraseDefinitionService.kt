package io.clhost.platform.eng.domain

import io.clhost.language.eng.Meaning
import io.clhost.language.eng.Translation
import org.springframework.stereotype.Component

@Component
class PhraseDefinitionService(
    private val phraseDefinitionRepository: PhraseDefinitionRepository
) {

    fun get(phrase: String) = phraseDefinitionRepository.get(phrase) ?: throw PhraseDefinitionNotFound(phrase)

    fun delete(phrase: String) = phraseDefinitionRepository.delete(phrase)

    fun create(phrase: String): PhraseDefinition {
        val phraseDefinition = PhraseDefinition(phrase).apply {
            initialize()
        }
        return phraseDefinition.apply { phraseDefinitionRepository.create(this) }
    }

    fun appendMeanings(phraseDefinition: PhraseDefinition, meanings: List<Meaning>) =
        phraseDefinition.apply {
            appendMeanings(meanings)
            phraseDefinitionRepository.update(this)
        }

    fun appendTranslations(phraseDefinition: PhraseDefinition, translations: List<Translation>) =
        phraseDefinition.apply {
            appendTranslations(translations)
            phraseDefinitionRepository.update(this)
        }

    fun appendTags(phraseDefinition: PhraseDefinition, tags: List<String>) =
        phraseDefinition.apply {
            appendTags(tags)
            phraseDefinitionRepository.update(this)
        }
}
