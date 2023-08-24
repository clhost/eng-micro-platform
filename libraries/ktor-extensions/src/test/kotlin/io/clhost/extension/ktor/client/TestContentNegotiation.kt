package io.clhost.extension.ktor.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.clhost.extension.test.wiremock.stubJsonGet
import io.clhost.extension.test.wiremock.stubJsonPost
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class TestContentNegotiation : ShouldSpec({

    val mapper = ObjectMapper().registerKotlinModule()

    val client = createKtorClient {
        useJson(mapper)
        useLogging()
    }

    should("unmarshall to test data class with GET request for application/json") {
        stubJsonGet("/test", 200) {
            """
                {
                    "data": "data"
                }
            """.trimIndent()
        }

        val testData = client.get("http://localhost:8080/test").body<TestData>()

        testData.data shouldBe "data"
    }

    should("unmarshall to test data class with POST request for application/json") {
        stubJsonPost("/test", 200) {
            """
                {
                    "data": "data"
                }
            """.trimIndent()
        }

        val testData = client.post("http://localhost:8080/test") {
            applicationJson()
            setBody(TestData("data"))
            setAttributes { }
        }.body<TestData>()

        testData.data shouldBe "data"
    }
})
