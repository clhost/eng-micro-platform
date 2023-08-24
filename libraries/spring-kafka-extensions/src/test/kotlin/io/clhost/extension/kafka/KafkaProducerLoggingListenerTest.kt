package io.clhost.extension.kafka

import io.mockk.impl.annotations.SpyK
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeaders
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.kafka.support.KafkaHeaders
import java.util.UUID

class KafkaProducerLoggingListenerTest {

    @SpyK
    var kafkaLogger = KafkaLogger()

    val correlationId = UUID.randomUUID().toString()
    val recordHeaders = RecordHeaders().add(KafkaHeaders.CORRELATION_ID, correlationId.toByteArray())

    val producerRecordWithHeader: ProducerRecord<String, Any> = ProducerRecord(
        "SOME_TOPIC",
        null,
        null,
        "some key",
        "{value}",
        recordHeaders
    )

    @Test
    fun `add correlation id when logger method called for producer`() {
        kafkaLogger.logConsumer("some json", mapOf())
        assertThat(MDC.get("correlationId"), `is`(notNullValue()))
    }

    @Test
    fun `pass correlation id to MDC if producer record has this header`() {
        kafkaLogger.logProducer(producerRecordWithHeader)
        assertThat(MDC.get("correlationId"), `is`(notNullValue()))
    }

    @Test
    fun `pass correlation id to MDC if producer record has this header and logger method called with an exception`() {
        kafkaLogger.logProducerError(producerRecordWithHeader, Exception("test exception"))
        assertThat(MDC.get("correlationId"), `is`(notNullValue()))
    }
}
