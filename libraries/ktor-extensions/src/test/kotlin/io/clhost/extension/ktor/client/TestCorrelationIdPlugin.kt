package io.clhost.extension.ktor.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import kotlinx.coroutines.async
import org.slf4j.MDC

class TestCorrelationIdPlugin : ShouldSpec({

    val mapper = ObjectMapper().registerKotlinModule()

    val engine = MockEngine { request ->
        respond(
            content = """{"data":"data"}""",
            headers = headers {
                appendAll(request.headers)
                append(HttpHeaders.ContentType, "application/json")
            },
            status = HttpStatusCode.OK
        )
    }

    val client = HttpClient(engine) {
        useJson(mapper)
        useLogging()
        useCorrelationId()
    }

    should("contain X-Correlation-ID header in both request and response") {
        val response = client.get("http://localhost:8080/test")
        response.headers.contains("X-Correlation-ID") shouldBe true
    }

    should("contain the same X-Correlation-ID header in all responses") {
        MDC.put("correlationId", "test")
        withPreservedCorrelationId {
            (1..10)
                .map { async { client.get("http://localhost:8080/test") } }
                .forEach { it.await().headers["X-Correlation-ID"] shouldBe "test" }
        }
    }

    should("contain the same X-Correlation-ID header in all responses (blocking call)") {
        MDC.put("correlationId", "test")
        runBlockingWithPreservedCorrelationId {
            (1..10)
                .map { async { client.get("http://localhost:8080/test") } }
                .forEach { it.await().headers["X-Correlation-ID"] shouldBe "test" }
        }
    }

    should("match X-Correlation-ID header pattern in all self generated responses") {
        val uuidRegex = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}\$"
        MDC.remove("correlationId")
        withPreservedCorrelationId {
            (1..10)
                .map { async { client.get("http://localhost:8080/test") } }
                .forEach { it.await().headers["X-Correlation-ID"] shouldMatch uuidRegex.toRegex() }
        }
    }
})
