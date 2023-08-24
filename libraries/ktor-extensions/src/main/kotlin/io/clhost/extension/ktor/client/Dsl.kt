package io.clhost.extension.ktor.client

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.apache.ApacheEngineConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.jackson.JacksonConverter
import io.micrometer.core.instrument.MeterRegistry
import java.time.Duration
import io.clhost.extension.ktor.client.plugin.MeasurementPluginConfig
import io.clhost.extension.ktor.client.plugin.Timers
import io.clhost.extension.ktor.client.plugin.createCorrelationPlugin
import io.clhost.extension.ktor.client.plugin.createMeasurementPlugin

fun HttpClientConfig<*>.useJson(mapper: ObjectMapper) {
    install(ContentNegotiation) {
        val converter = JacksonConverter(mapper, true)
        register(ContentType.Application.Json, converter)
    }
}

fun HttpClientConfig<*>.useLogging(logBody: Boolean = true) {
    install(Logging) {
        this.level = if (logBody) LogLevel.ALL else LogLevel.HEADERS
    }
}

fun HttpClientConfig<*>.useTimeouts(request: Long, connect: Long, socket: Long) {
    install(HttpTimeout) {
        this.socketTimeoutMillis = socket
        this.requestTimeoutMillis = request
        this.connectTimeoutMillis = connect
    }
}

fun HttpClientConfig<*>.useMeasurement(scopeName: String, meterRegistry: MeterRegistry) {
    install(createMeasurementPlugin { MeasurementPluginConfig(Timers(scopeName, meterRegistry)) })
}

fun HttpClientConfig<*>.useCorrelationId() {
    install(createCorrelationPlugin())
}

fun HttpClientConfig<ApacheEngineConfig>.usePoolConnections(maxConnTotal: Int, maxConnPerRoute: Int) {
    connectionPool {
        this.maxConnTotal = maxConnTotal
        this.maxConnPerRoute = maxConnPerRoute
    }
}

fun HttpClientConfig<ApacheEngineConfig>.useKeepAlive(keepAlive: Duration) {
    keepALive {
        this.keepAlive = keepAlive
    }
}
