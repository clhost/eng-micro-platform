package io.clhost.extension.ktor.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.clhost.extension.ktor.client.plugin.label
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import java.time.Duration
import io.clhost.extension.test.wiremock.stubJsonGet
import io.clhost.extension.test.wiremock.stubJsonPost

class FullClientTest : ShouldSpec({

    val mapper = ObjectMapper().registerKotlinModule()
    val meterRegistry = SimpleMeterRegistry()

    val client = createKtorClient {
        useJson(mapper)
        useLogging()
        useCorrelationId()
        useTimeouts(1000, 1000, 1000)
        useMeasurement("test-scope", meterRegistry)
        usePoolConnections(100, 100)
        useKeepAlive(Duration.ofSeconds(50))
    }

    should("unmarshall to test data class with GET request") {
        stubJsonGet("/test", 200) {
            """
                {
                    "data": "data"
                }
            """.trimIndent()
        }

        val testData = client.get("http://localhost:8080/test") { label("test") }.body<TestData>()

        testData.data shouldBe "data"
    }

    should("unmarshall to test data class with POST request") {
        stubJsonPost("/test", 200) {
            """
                {
                    "data": "data"
                }
            """.trimIndent()
        }

        val testData = client.post("http://localhost:8080/test") {
            label("test")
            applicationJson()
            setBody(TestData("data"))
        }.body<TestData>()

        testData.data shouldBe "data"
    }
})
