package io.clhost.extension.kafka

import java.util.UUID
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.kafka.support.KafkaHeaders

class KafkaLogger {

    private val logger = LoggerFactory.getLogger(KafkaLogger::class.java)

    fun logConsumer(payload: String, headers: Map<String, Any>) {
        val correlationId = if (headers[KafkaHeaders.CORRELATION_ID] == null) UUID.randomUUID().toString()
        else String(headers[KafkaHeaders.CORRELATION_ID] as ByteArray)

        val message = """
            |Logging Kafka message from ${headers["kafka_receivedTopic"]}
            |CorrelationId: $correlationId
            |Body: $payload
        """.trimMargin()

        MDC.put("correlationId", correlationId)
        logger.info(message)
    }

    fun logProducer(producerRecord: ProducerRecord<String, Any>) {
        val header = producerRecord.headers()?.lastHeader(KafkaHeaders.CORRELATION_ID)
        val correlationId = header?.value()?.let { String(it) }
        val message = """
            |Send Kafka message to ${producerRecord.topic()}
            |CorrelationId: $correlationId
            |Key: ${producerRecord.key()}
            |Partition: ${producerRecord.partition()}
            |Body: ${producerRecord.value()}
        """.trimMargin()
        logger.info(message)
    }

    fun logProducerError(producerRecord: ProducerRecord<String, Any>, exception: Exception?) {
        val header = producerRecord.headers()?.lastHeader(KafkaHeaders.CORRELATION_ID)
        val correlationId = header?.value()?.let { String(it) }
        val message = """
            |Can't send Kafka message to ${producerRecord.topic()}
            |CorrelationId: $correlationId
            |Key: ${producerRecord.key()}
            |Partition: ${producerRecord.partition()}
            |Body: ${producerRecord.value()}
        """.trimMargin()
        logger.error(message, exception)
    }
}
