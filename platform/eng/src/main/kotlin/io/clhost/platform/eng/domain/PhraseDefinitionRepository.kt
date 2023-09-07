package io.clhost.platform.eng.domain

interface PhraseDefinitionRepository {

    fun get(phrase: String): PhraseDefinition?
    fun delete(phrase: String)
    fun create(phraseDefinition: PhraseDefinition)
    fun update(phraseDefinition: PhraseDefinition)
}
