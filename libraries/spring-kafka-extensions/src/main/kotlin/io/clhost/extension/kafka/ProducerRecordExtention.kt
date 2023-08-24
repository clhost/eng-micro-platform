package io.clhost.extension.kafka

import java.util.UUID
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.MDC
import org.springframework.kafka.support.KafkaHeaders

fun ProducerRecord<String, Any>.setCorrelationId() {
    val correlationId = MDC.get("correlationId") ?: UUID.randomUUID().toString()
    MDC.put("correlationId", correlationId)
    headers()?.add(KafkaHeaders.CORRELATION_ID, correlationId.toByteArray())
}
