package io.clhost.extension.ktor.client.plugin

import io.ktor.client.plugins.api.SendingRequest
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.util.AttributeKey
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Timer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

private val measurementInfo = AttributeKey<Pair<String, List<Tag>>>("measurementInfo")

fun HttpRequestBuilder.label(name: String, tags: List<Tag> = listOf()) {
    attributes.put(measurementInfo, name to tags)
}

fun createMeasurementPlugin(
    config: () -> MeasurementPluginConfig
) = createClientPlugin("MeasurementPlugin", config) {
    val timers = pluginConfig.timers
    val key = AttributeKey<Long>("requestStartTimeInMillis")

    on(SendingRequest) { request, _ ->
        if (request.attributes.contains(measurementInfo)) {
            request.attributes.put(key, System.currentTimeMillis())
        }
    }

    onResponse { response ->
        if (response.call.attributes.contains(measurementInfo)) {
            val onCallTime = response.call.attributes[key]
            val onCallReceiveTime = System.currentTimeMillis()
            val info = response.call.attributes[measurementInfo]
            timers.measure(info.first, info.second, onCallReceiveTime - onCallTime)
        }
    }
}

class MeasurementPluginConfig(
    val timers: Timers
)

class Timers(
    private val scopeName: String,
    private val meterRegistry: MeterRegistry
) {

    private val timers = ConcurrentHashMap<String, Timer>()

    fun measure(label: String, tags: List<Tag>, time: Long) {
        timer(label, tags).record(time, TimeUnit.MILLISECONDS)
    }

    private fun timer(label: String, tags: List<Tag>): Timer {
        if (timers[label] != null) return timers[label]!!
        timers[label] = Timer.builder(scopeName)
            .tag("method_name", label)
            .tags(tags)
            .publishPercentiles(0.5, 0.75, 0.95, 0.99)
            .register(meterRegistry)
        return timers[label]!!
    }
}
