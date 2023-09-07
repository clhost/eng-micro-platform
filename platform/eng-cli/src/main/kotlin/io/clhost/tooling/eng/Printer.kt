package io.clhost.tooling.eng

import io.clhost.language.eng.PhraseDefinitionDto
import io.clhost.language.eng.WordDefinitionDto
import io.clhost.tooling.extensions.lightCyan

fun printWordDefinition(wordDefinition: WordDefinitionDto) {
    println("${"Word: ".lightCyan} ${wordDefinition.word}")

    println("Translations:".lightCyan)
    wordDefinition.translations.forEach {
        println("    - ${"Definition:".lightCyan} ${it.definition}")
        println("      ${"Source:".lightCyan} ${it.source}")
        println("      ${"Language:".lightCyan} ${it.language}")
    }

    println("Pronunciations:".lightCyan)
    wordDefinition.pronunciations.forEach {
        println("    - ${"Ipa:".lightCyan} ${it.ipa}")
        println("      ${"Source:".lightCyan} ${it.source}")
        println("      ${"Audio url:".lightCyan} ${it.audioUrl}")
    }

    println("Meanings:".lightCyan)
    wordDefinition.meanings.forEach {
        println("    - ${"Part of speech:".lightCyan} ${it.partOfSpeech}")
        println("      ${"Source:".lightCyan} ${it.source}")
        println("      ${"Description:".lightCyan} ${it.description.replace("([\r\n])+".toRegex(), "; ")}")
        println("      ${"Example:".lightCyan} ${it.example?.replace("([\r\n])+".toRegex(), "; ")}")
    }

    println("${"Tags:".lightCyan} ${wordDefinition.tags}")
    println("${"Synonyms:".lightCyan} ${wordDefinition.synonyms}")
}

fun printPhraseDefinition(phraseDefinition: PhraseDefinitionDto) {
    println("${"Phrase: ".lightCyan} ${phraseDefinition.phrase}")

    println("Translations:".lightCyan)
    phraseDefinition.translations.forEach {
        println("    - ${"Definition:".lightCyan} ${it.definition}")
        println("      ${"Source:".lightCyan} ${it.source}")
        println("      ${"Language:".lightCyan} ${it.language}")
    }

    println("Meanings:".lightCyan)
    phraseDefinition.meanings.forEach {
        println("    - ${"Part of speech:".lightCyan} ${it.partOfSpeech}")
        println("      ${"Source:".lightCyan} ${it.source}")
        println("      ${"Description:".lightCyan} ${it.description.replace("([\r\n])+".toRegex(), "; ")}")
        println("      ${"Example:".lightCyan} ${it.example?.replace("([\r\n])+".toRegex(), "; ")}")
    }

    println("${"Tags:".lightCyan} ${phraseDefinition.tags}")
}
