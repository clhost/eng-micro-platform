package io.clhost.tooling.eng

import io.clhost.tooling.extensions.lightCyan

fun printWordDefinition(wordDefinition: WordDefinition) {
    println("${"Word: ".lightCyan} ${wordDefinition.word}")

    println("Translations:".lightCyan)
    wordDefinition.translations.forEach {
        println("    - ${"Translation:".lightCyan} ${it.word}")
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
    }

    println("Examples:".lightCyan)
    wordDefinition.examples.forEach {
        println("    - ${"Source:".lightCyan} ${it.source}")
        println("      ${"Definition:".lightCyan} ${it.definition.replace("([\r\n])+".toRegex(), "; ")}")
    }

    println("${"Tags:".lightCyan} ${wordDefinition.tags}")
    println("${"Synonyms:".lightCyan} ${wordDefinition.synonyms}")
}
