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

// todo: should add Yandex Dictionary API (because translation api is very poor)
// todo: registration and identification
// todo: determination of part of speech (idiom, phrasal verb etc)
