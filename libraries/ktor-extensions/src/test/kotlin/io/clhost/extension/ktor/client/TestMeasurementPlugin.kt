package io.clhost.extension.ktor.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.clhost.extension.ktor.client.plugin.label
import io.clhost.extension.test.wiremock.stubJsonGet
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.micrometer.core.instrument.search.MeterNotFoundException
import io.micrometer.core.instrument.simple.SimpleMeterRegistry

class TestMeasurementPlugin : ShouldSpec({

    val mapper = ObjectMapper().registerKotlinModule()

    should("record time when the label is specified") {
        stubJsonGet("/test", 200, ::String)

        val meterRegistry = SimpleMeterRegistry()

        val client = createKtorClient {
            useJson(mapper)
            useLogging()
            useMeasurement("test-scope", meterRegistry)
        }

        client.get("http://localhost:8080/test") { label("testClient") }

        meterRegistry.get("test-scope").meter().id.tags.any { it.value == "testClient" } shouldBe true
    }

    should("not record time when the label is not specified") {
        stubJsonGet("/test", 200, ::String)

        val meterRegistry = SimpleMeterRegistry()

        val client = createKtorClient {
            useJson(mapper)
            useLogging()
            useMeasurement("test-scope", meterRegistry)
        }

        client.get("http://localhost:8080/test")

        shouldThrow<MeterNotFoundException> { meterRegistry.get("test-scope").meter() }
    }

    should("record time when the label is specified and an exception is thrown") {
        stubJsonGet("/test", 400) { "" }

        val meterRegistry = SimpleMeterRegistry()

        val client = createKtorClient {
            useJson(mapper)
            useLogging()
            useMeasurement("test-scope", meterRegistry)
        }

        shouldThrow<ClientRequestException> { client.get("http://localhost:8080/test") { label("testClient") } }

        meterRegistry.get("test-scope").meter().id.tags.any { it.value == "testClient" } shouldBe true
    }
})
