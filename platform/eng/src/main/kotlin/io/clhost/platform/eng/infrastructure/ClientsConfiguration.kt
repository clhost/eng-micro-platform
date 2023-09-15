package io.clhost.platform.eng.infrastructure

import io.clhost.extension.jackson.standardObjectMapper
import io.clhost.extension.ktor.client.createKtorClient
import io.clhost.extension.ktor.client.useCorrelationId
import io.clhost.extension.ktor.client.useJson
import io.clhost.extension.ktor.client.useLogging
import io.clhost.extension.ktor.client.useMeasurement
import io.clhost.extension.ktor.client.usePoolConnections
import io.clhost.extension.ktor.client.useTimeouts
import io.micrometer.core.instrument.MeterRegistry
import java.net.URI
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ClientsConfiguration(
    private val meterRegistry: MeterRegistry
) {

    @Bean
    fun urbanDictionaryKtorClient(
        @Value("\${client.urban-dictionary.url}")
        url: String
    ) = createKtorClient {
        useLogging(logBody = false)
        useCorrelationId()
        useTimeouts(10000, 10000, 10000)
        usePoolConnections(20, 20)
        useJson(standardObjectMapper)
        useMeasurement(URI(url).host, meterRegistry)
    }

    @Bean
    fun dictionaryKtorClient(
        @Value("\${client.dictionary.direct-url}")
        url: String
    ) = createKtorClient {
        useLogging(logBody = false)
        useCorrelationId()
        useTimeouts(10000, 10000, 10000)
        usePoolConnections(20, 20)
        useJson(standardObjectMapper)
        useMeasurement(URI(url).host, meterRegistry)
    }

    @Bean
    fun yandexCloudKtorClient() = createKtorClient {
        useLogging(logBody = true)
        useCorrelationId()
        useTimeouts(10000, 10000, 10000)
        usePoolConnections(20, 20)
        useJson(standardObjectMapper)
        useMeasurement("cloud.yandex", meterRegistry)
    }

    @Bean
    fun merriamWebsterKtorClient() = createKtorClient {
        useLogging(logBody = false)
        useCorrelationId()
        useTimeouts(10000, 10000, 10000)
        usePoolConnections(20, 20)
        useJson(standardObjectMapper)
        useMeasurement("merriam.webster", meterRegistry)
    }
}
