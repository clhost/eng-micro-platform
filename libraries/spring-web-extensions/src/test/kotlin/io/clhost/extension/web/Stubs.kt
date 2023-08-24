package io.clhost.extension.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
open class TestApplication

@Validated
@RestController
class TestRestController {

    @GetMapping("/get")
    fun get() = "ok"

    @PostMapping("/post")
    fun post(
        @RequestBody
        testItem: TestItem
    ) = "ok for $testItem"
}

data class TestItem(
    val key: String,
    val value: String
)
