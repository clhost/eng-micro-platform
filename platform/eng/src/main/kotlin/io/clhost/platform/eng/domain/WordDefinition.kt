package io.clhost.platform.eng.domain

import io.clhost.language.eng.Meaning
import io.clhost.language.eng.Pronunciation
import io.clhost.language.eng.Translation
import java.time.OffsetDateTime

class WordDefinition(
    val word: String
) {

    init {
        if (!word.matches("\\w+".toRegex())) {
            throw WordViolatedInvariant.shouldBeWord(word)
        }
    }

    private constructor(
        word: String,
        createdAt: OffsetDateTime,
        updatedAt: OffsetDateTime,
        meanings: List<Meaning>,
        translations: List<Translation>,
        pronunciations: List<Pronunciation>,
        tags: List<String>,
        synonyms: List<String>
    ) : this(word) {
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.meanings.addAll(meanings)
        this.translations.addAll(translations)
        this.pronunciations.addAll(pronunciations)
        this.tags.addAll(tags)
        this.synonyms.addAll(synonyms)
    }

    companion object {
        fun reflect(
            word: String,
            createdAt: OffsetDateTime,
            updatedAt: OffsetDateTime,
            meanings: List<Meaning>,
            translations: List<Translation>,
            pronunciations: List<Pronunciation>,
            tags: List<String>,
            synonyms: List<String>
        ) = WordDefinition(word, createdAt, updatedAt, meanings, translations, pronunciations, tags, synonyms)
    }

    lateinit var createdAt: OffsetDateTime; private set
    lateinit var updatedAt: OffsetDateTime; private set

    var meanings = mutableSetOf<Meaning>(); private set
    var translations = mutableSetOf<Translation>(); private set
    var pronunciations = mutableSetOf<Pronunciation>(); private set

    var tags = mutableSetOf<String>(); private set
    var synonyms = mutableSetOf<String>(); private set

    fun initialize() {
        this.createdAt = OffsetDateTime.now()
        this.updatedAt = this.createdAt
    }

    fun appendMeanings(meanings: List<Meaning>) {
        this.meanings.addAll(meanings)
        this.updatedAt = OffsetDateTime.now()
    }

    fun appendTranslations(translations: List<Translation>) {
        this.translations.addAll(translations)
        this.updatedAt = OffsetDateTime.now()
    }

    fun appendPronunciations(pronunciations: List<Pronunciation>) {
        this.pronunciations.addAll(pronunciations)
        this.updatedAt = OffsetDateTime.now()
    }

    fun appendTags(tags: List<String>) {
        this.tags.addAll(tags)
        this.updatedAt = OffsetDateTime.now()
    }

    fun appendSynonyms(synonyms: List<String>) {
        this.synonyms.addAll(synonyms)
        this.updatedAt = OffsetDateTime.now()
    }
}
