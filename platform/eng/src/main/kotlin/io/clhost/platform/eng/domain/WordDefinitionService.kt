package io.clhost.platform.eng.domain

import io.clhost.language.eng.Meaning
import io.clhost.language.eng.Pronunciation
import io.clhost.language.eng.Translation
import org.springframework.stereotype.Component

@Component
class WordDefinitionService(
    private val wordDefinitionRepository: WordDefinitionRepository
) {

    fun get(word: String) = wordDefinitionRepository.get(word) ?: throw WordDefinitionNotFound(word)

    fun delete(word: String) = wordDefinitionRepository.delete(word)

    fun create(word: String): WordDefinition {
        val wordDefinition = WordDefinition(word).apply {
            initialize()
        }
        return wordDefinition.apply { wordDefinitionRepository.create(this) }
    }

    fun appendMeanings(wordDefinition: WordDefinition, meanings: List<Meaning>) =
        wordDefinition.apply {
            appendMeanings(meanings)
            wordDefinitionRepository.update(this)
        }

    fun appendTranslations(wordDefinition: WordDefinition, translations: List<Translation>) =
        wordDefinition.apply {
            appendTranslations(translations)
            wordDefinitionRepository.update(this)
        }

    fun appendPronunciations(wordDefinition: WordDefinition, pronunciations: List<Pronunciation>) =
        wordDefinition.apply {
            appendPronunciations(pronunciations)
            wordDefinitionRepository.update(this)
        }

    fun appendTags(wordDefinition: WordDefinition, tags: List<String>) =
        wordDefinition.apply {
            appendTags(tags)
            wordDefinitionRepository.update(this)
        }

    fun appendSynonyms(wordDefinition: WordDefinition, synonyms: List<String>) =
        wordDefinition.apply {
            appendSynonyms(synonyms)
            wordDefinitionRepository.update(this)
        }
}
