package io.clhost.extension.kafka

import org.apache.kafka.clients.producer.ProducerInterceptor
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata

class KafkaProducerLoggingInterceptor : ProducerInterceptor<String, Any> {

    override fun onSend(
        record: ProducerRecord<String, Any>
    ): ProducerRecord<String, Any> = record.apply { setCorrelationId() }

    override fun configure(configs: MutableMap<String, *>?) {}

    override fun onAcknowledgement(metadata: RecordMetadata?, exception: Exception?) {}

    override fun close() {}
}
