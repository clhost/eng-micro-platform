package io.clhost.platform.eng.domain

import io.clhost.language.faults.BusinessLogicException

class WordDefinitionNotFound(
    word: String
) : BusinessLogicException(
    "wordDefinitionNotFound",
    "Word definition not found (word=$word)"
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
