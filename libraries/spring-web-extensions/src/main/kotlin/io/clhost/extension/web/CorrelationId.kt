package io.clhost.extension.web

import java.util.UUID
import org.slf4j.MDC

object CorrelationId {

    const val headerName = "X-Correlation-ID"

    fun generate() = UUID.randomUUID().toString()

    fun get() = MDC.get("correlationId")

    fun save(correlationId: String) = MDC.put("correlationId", correlationId)

    fun remove() = MDC.remove("correlationId")
}
