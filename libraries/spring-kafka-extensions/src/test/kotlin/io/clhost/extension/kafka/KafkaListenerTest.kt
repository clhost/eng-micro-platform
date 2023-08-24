package io.clhost.extension.kafka

import io.kotest.core.spec.style.ShouldSpec
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Duration
import org.springframework.kafka.support.Acknowledgment

class KafkaListenerTest : ShouldSpec({

    should("acknowledge when listener doesn't throw exception") {
        val acknowledgment = mockk<Acknowledgment>(relaxed = true)

        listenKafka<String>(
            acknowledgment = acknowledgment,
            payload = "\"payload\"",
            nackOnExceptions = listOf(),
            listener = {}
        )

        verify(exactly = 1) { acknowledgment.acknowledge() }
        verify(exactly = 0) { acknowledgment.nack(Duration.ofMillis(60)) }

        confirmVerified(acknowledgment)
    }

    should("acknowledge when listener throw ackacble exception") {
        val acknowledgment = mockk<Acknowledgment>(relaxed = true)

        listenKafka<String>(
            acknowledgment = acknowledgment,
            payload = "\"payload\"",
            nackOnExceptions = listOf(),
            listener = { throw IllegalArgumentException() }
        )

        verify(exactly = 1) { acknowledgment.acknowledge() }
        verify(exactly = 0) { acknowledgment.nack(Duration.ofMillis(60)) }

        confirmVerified(acknowledgment)
    }

    should("nack when listener throw nackacble exception") {
        val acknowledgment = mockk<Acknowledgment>(relaxed = true)

        listenKafka<String>(
            acknowledgment = acknowledgment,
            payload = "\"payload\"",
            nackOnExceptions = listOf(IllegalArgumentException::class),
            listener = { throw IllegalArgumentException() }
        )

        verify(exactly = 0) { acknowledgment.acknowledge() }
        verify(exactly = 1) { acknowledgment.nack(Duration.ofMillis(60)) }

        confirmVerified(acknowledgment)
    }

    should("nack when listener throw nackacble child exception") {
        val acknowledgment = mockk<Acknowledgment>(relaxed = true)

        listenKafka<String>(
            acknowledgment = acknowledgment,
            payload = "\"payload\"",
            nackOnExceptions = listOf(RuntimeException::class),
            listener = { throw IllegalArgumentException() }
        )

        verify(exactly = 0) { acknowledgment.acknowledge() }
        verify(exactly = 1) { acknowledgment.nack(Duration.ofMillis(60)) }

        confirmVerified(acknowledgment)
    }

    should("acknowledge when listener throw ackacble exception and invoke onAckableError") {
        val acknowledgment = mockk<Acknowledgment>(relaxed = true)
        val onAckableErrorStub = mockk<Stub>(relaxed = true)
        val onNackableErrorStub = mockk<Stub>(relaxed = true)

        listenKafka<String>(
            acknowledgment = acknowledgment,
            payload = "\"payload\"",
            nackOnExceptions = listOf(),
            onAckableError = { onAckableErrorStub.stub() },
            onNackableError = { onNackableErrorStub.stub() },
            listener = { throw IllegalArgumentException() }
        )

        verify(exactly = 1) { acknowledgment.acknowledge() }
        verify(exactly = 0) { acknowledgment.nack(Duration.ofMillis(60)) }

        verify(exactly = 1) { onAckableErrorStub.stub() }
        verify(exactly = 0) { onNackableErrorStub.stub() }

        confirmVerified(acknowledgment)
        confirmVerified(onAckableErrorStub)
        confirmVerified(onNackableErrorStub)
    }

    should("nack when listener throw nackacble exception and invoke onNackableError") {
        val acknowledgment = mockk<Acknowledgment>().apply {
            every { nack(Duration.ofMillis(60)) } returns Unit
            every { acknowledge() } returns Unit
        }

        val onAckableErrorStub = mockk<Stub>().apply {
            every { stub() } returns Unit
        }

        val onNackableErrorStub = mockk<Stub>().apply {
            every { stub() } returns Unit
        }

        listenKafka<String>(
            acknowledgment = acknowledgment,
            payload = "\"payload\"",
            nackOnExceptions = listOf(IllegalArgumentException::class),
            onAckableError = { onAckableErrorStub.stub() },
            onNackableError = { onNackableErrorStub.stub() },
            listener = { throw IllegalArgumentException() }
        )

        verify(exactly = 0) { acknowledgment.acknowledge() }
        verify(exactly = 1) { acknowledgment.nack(Duration.ofMillis(60)) }

        verify(exactly = 0) { onAckableErrorStub.stub() }
        verify(exactly = 1) { onNackableErrorStub.stub() }

        confirmVerified(acknowledgment)
        confirmVerified(onAckableErrorStub)
        confirmVerified(onNackableErrorStub)
    }
})

class Stub {
    fun stub() {}
}
