package io.clhost.platform.eng.domain

import io.clhost.language.faults.BusinessLogicException

class WordDefinitionNotFound(
    word: String
) : BusinessLogicException(
    "wordDefinitionNotFound",
    "Word definition not found (word=$word)"
)

class PhraseDefinitionNotFound(
    phrase: String
) : BusinessLogicException(
    "phraseDefinitionNotFound",
    "Phrase definition not found (phrase=$phrase)"
)

class WordViolatedInvariant(
    word: String,
    message: String
) : BusinessLogicException(
    "wordViolatedInvariant",
    "Word invariant is violated (word=$word, message=$message)"
) {
    companion object {
        fun shouldBeWord(word: String) = WordViolatedInvariant(word, "Should be word")
    }
}

class PhraseViolatedInvariant(
    phrase: String,
    message: String
) : BusinessLogicException(
    "phraseViolatedInvariant",
    "Phrase invariant is violated (phrase=$phrase, message=$message)"
) {
    companion object {
        fun shouldBePhrase(phrase: String) = PhraseViolatedInvariant(phrase, "Should be phrase")
    }
}
