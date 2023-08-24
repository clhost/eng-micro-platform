package io.clhost.starter.kafka

import io.clhost.extension.kafka.KafkaProducerLoggingListener
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate

@Configuration
@ConditionalOnClass(KafkaTemplate::class)
@ConditionalOnProperty(
    value = ["spring-kafka-extensions.producer.logging.enabled"],
    havingValue = "true",
    matchIfMissing = true
)
open class KafkaExtensionsAutoConfiguration {

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @PostConstruct
    open fun withLogging() {
        kafkaTemplate.setProducerListener(KafkaProducerLoggingListener())
    }
}
