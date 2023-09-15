package io.clhost.tooling.eng

import io.clhost.extension.cli.read
import io.clhost.extension.cli.yamlDecoded
import io.clhost.extension.cli.yamlEncoded
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

val config: EngConfig by lazy {
    File(ENG_CONFIG_FILE).read().yamlDecoded()
}

fun EngConfig.encoded() = yamlEncoded()

fun EngConfig.updateEngMicroPlatformHost(engMicroPlatformHost: String): EngConfig {
    this.engMicroPlatformHost = engMicroPlatformHost
    persist()
    return this
}

@Serializable
class EngConfig(
    var engMicroPlatformPort: Int,
    var engMicroPlatformHost: String,
    @Transient
    var engMicroPlatformUrl: String = "http://$engMicroPlatformHost:$engMicroPlatformPort"
)

private fun EngConfig.persist() = File(ENG_CONFIG_FILE).writeText(yamlEncoded())
