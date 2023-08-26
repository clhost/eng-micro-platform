package io.clhost.platform.eng.domain

interface WordDefinitionRepository {

    fun get(word: String): WordDefinition?
    fun create(wordDefinition: WordDefinition)
    fun update(wordDefinition: WordDefinition)
}
