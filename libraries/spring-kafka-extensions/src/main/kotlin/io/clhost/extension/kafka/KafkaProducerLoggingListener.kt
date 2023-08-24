package io.clhost.extension.kafka

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.springframework.kafka.support.ProducerListener

open class KafkaProducerLoggingListener : ProducerListener<String, Any> {

    private val kafkaLogger = KafkaLogger()

    override fun onSuccess(
        producerRecord: ProducerRecord<String, Any>,
        recordMetadata: RecordMetadata?
    ) = kafkaLogger.logProducer(producerRecord)

    override fun onError(
        producerRecord: ProducerRecord<String, Any>,
        recordMetadata: RecordMetadata?,
        exception: Exception?
    ) = kafkaLogger.logProducerError(producerRecord, exception)
}
