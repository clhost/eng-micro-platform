package io.clhost.platform.telegram.infrastructure

import io.clhost.extension.jackson.standardObjectMapper
import io.clhost.extension.ktor.client.createKtorClient
import io.clhost.extension.ktor.client.useCorrelationId
import io.clhost.extension.ktor.client.useJson
import io.clhost.extension.ktor.client.useMeasurement
import io.clhost.extension.ktor.client.usePoolConnections
import io.clhost.extension.ktor.client.useTimeouts
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ClientsConfiguration(
    private val meterRegistry: MeterRegistry
) {

    @Bean
    fun telegramBotKtorClient() = createKtorClient {
        useCorrelationId()
        useTimeouts(10000, 10000, 10000)
        usePoolConnections(20, 20)
        useJson(standardObjectMapper)
        useMeasurement("api.telegram", meterRegistry)
    }
}
