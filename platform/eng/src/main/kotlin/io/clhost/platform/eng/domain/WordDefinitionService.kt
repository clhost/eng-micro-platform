package io.clhost.platform.eng.domain

import org.springframework.stereotype.Component

@Component
class WordDefinitionService(
    private val wordDefinitionRepository: WordDefinitionRepository
) {

    fun get(word: String) = wordDefinitionRepository.get(word) ?: throw WordDefinitionNotFound(word)

    fun create(word: String): WordDefinition {
        val wordDefinition = WordDefinition(word).apply {
            initialize()
        }
        return wordDefinition.apply { wordDefinitionRepository.create(this) }
    }

    fun appendExamples(wordDefinition: WordDefinition, examples: List<Example>) =
        wordDefinition.apply {
            wordDefinition.appendExamples(examples)
            wordDefinitionRepository.update(this)
        }

    fun appendMeanings(wordDefinition: WordDefinition, meanings: List<Meaning>) =
        wordDefinition.apply {
            wordDefinition.appendMeanings(meanings)
            wordDefinitionRepository.update(this)
        }

    fun appendTranslations(wordDefinition: WordDefinition, translations: List<Translation>) =
        wordDefinition.apply {
            wordDefinition.appendTranslations(translations)
            wordDefinitionRepository.update(this)
        }

    fun appendPronunciations(wordDefinition: WordDefinition, pronunciations: List<Pronunciation>) =
        wordDefinition.apply {
            wordDefinition.appendPronunciations(pronunciations)
            wordDefinitionRepository.update(this)
        }

    fun appendTags(wordDefinition: WordDefinition, tags: List<String>) =
        wordDefinition.apply {
            wordDefinition.appendTags(tags)
            wordDefinitionRepository.update(this)
        }

    fun appendSynonyms(wordDefinition: WordDefinition, synonyms: List<String>) =
        wordDefinition.apply {
            wordDefinition.appendSynonyms(synonyms)
            wordDefinitionRepository.update(this)
        }
}
