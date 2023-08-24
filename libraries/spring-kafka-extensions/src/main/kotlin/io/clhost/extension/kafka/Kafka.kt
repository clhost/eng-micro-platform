package io.clhost.extension.kafka

import io.clhost.extension.jackson.jsonDecode
import java.time.Duration
import kotlin.reflect.KClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.Acknowledgment
import io.clhost.extension.kafka.KafkaListenerExtension.kafkaLogger
import io.clhost.extension.kafka.KafkaListenerExtension.logger

object KafkaListenerExtension {
    val logger: Logger = LoggerFactory.getLogger(KafkaLogger::class.java)
    val kafkaLogger: KafkaLogger = KafkaLogger()
}

inline fun <reified T> listenKafka(
    payload: String,
    acknowledgment: Acknowledgment,
    nackOnExceptions: List<KClass<out Exception>>,
    headers: Map<String, Any> = mapOf(),
    nackSleepTime: Duration = Duration.ofMillis(60),
    isPayloadWillBeLogged: Boolean = true,
    onAckableError: (Exception) -> Unit = {},
    onNackableError: (Exception) -> Unit = {},
    crossinline listener: (T) -> Unit
) {
    try {
        if (isPayloadWillBeLogged) kafkaLogger.logConsumer(payload, headers)
        listener(jsonDecode(payload))
        acknowledgment.acknowledge()
    } catch (ex: Exception) {
        if (nackOnExceptions.any { it.isInstance(ex) }) {
            logger.error("Exception occurred for nackable exception: payload={}", payload, ex)
            onNackableError(ex)
            acknowledgment.nack(nackSleepTime)
            return
        }
        logger.error("Exception occurred for ackable exception: payload={}", payload, ex)
        onAckableError(ex)
        acknowledgment.acknowledge()
    }
}
