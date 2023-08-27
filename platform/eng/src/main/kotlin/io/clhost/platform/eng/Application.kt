package io.clhost.platform.eng

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

// todo: refactoring - should extract Source
// todo: examples from dictionary.com
// todo: partOfSpeech became nullable because urbandictionary doesn't define it
// todo: Pronunciation#audioUrl became nullable
