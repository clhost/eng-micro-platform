package io.clhost.platform.eng.domain

import io.clhost.language.eng.Meaning
import io.clhost.language.eng.Pronunciation
import io.clhost.language.eng.Translation
import java.time.OffsetDateTime

class PhraseDefinition(
    val phrase: String
) {

    init {
        phrase.split("\\s+".toRegex()).forEach {
            if (!it.matches("(\\w+-?\\w+)".toRegex())) {
                throw PhraseViolatedInvariant.shouldBePhrase(phrase)
            }
        }
    }

    private constructor(
        phrase: String,
        createdAt: OffsetDateTime,
        updatedAt: OffsetDateTime,
        meanings: List<Meaning>,
        translations: List<Translation>,
        tags: List<String>
    ) : this(phrase) {
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.meanings.addAll(meanings)
        this.translations.addAll(translations)
        this.tags.addAll(tags)
    }

    companion object {
        fun reflect(
            phrase: String,
            createdAt: OffsetDateTime,
            updatedAt: OffsetDateTime,
            meanings: List<Meaning>,
            translations: List<Translation>,
            tags: List<String>
        ) = PhraseDefinition(phrase, createdAt, updatedAt, meanings, translations, tags)
    }

    lateinit var createdAt: OffsetDateTime; private set
    lateinit var updatedAt: OffsetDateTime; private set

    var meanings = mutableSetOf<Meaning>(); private set
    var translations = mutableSetOf<Translation>(); private set

    var tags = mutableSetOf<String>(); private set

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

    fun appendTags(tags: List<String>) {
        this.tags.addAll(tags)
        this.updatedAt = OffsetDateTime.now()
    }
}
