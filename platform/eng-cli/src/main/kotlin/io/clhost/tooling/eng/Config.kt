package io.clhost.tooling.eng

import io.clhost.extension.cli.read
import io.clhost.extension.cli.yamlDecoded
import io.clhost.extension.cli.yamlEncoded
import java.io.File
import kotlinx.serialization.Serializable

val config: EngConfig by lazy {
    File(ENG_CONFIG_FILE).read().yamlDecoded()
}

fun EngConfig.encoded() = yamlEncoded()

fun EngConfig.useMinikubeIpForEngMicroPlatformUrl(minikubeIp: String): EngConfig {
    this.engMicroPlatformUrl = "http://$minikubeIp:32000"
    persist()
    return this
}

fun EngConfig.useLocalEngMicroPlatformUrl(): EngConfig {
    this.engMicroPlatformUrl = "http://localhost:11111"
    persist()
    return this
}

@Serializable
class EngConfig(
    var engMicroPlatformUrl: String
)

private fun EngConfig.persist() = File(ENG_CONFIG_FILE).writeText(yamlEncoded())
