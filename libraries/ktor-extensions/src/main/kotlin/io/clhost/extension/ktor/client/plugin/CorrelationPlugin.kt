package io.clhost.extension.ktor.client.plugin

import io.ktor.client.plugins.api.createClientPlugin
import java.util.UUID
import org.slf4j.MDC

fun createCorrelationPlugin() = createClientPlugin("CorrelationPlugin") {
    onRequest { request, _ ->
        request.headers.append("X-Correlation-ID", MDC.get("correlationId") ?: generateAndSetCorrelationId())
    }
}

private fun generateAndSetCorrelationId() = UUID.randomUUID().toString().apply { MDC.put("correlationId", this) }
