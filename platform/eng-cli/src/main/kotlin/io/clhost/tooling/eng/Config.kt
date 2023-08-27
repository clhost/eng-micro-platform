package io.clhost.tooling.eng

import io.clhost.tooling.extensions.read
import io.clhost.tooling.extensions.yamlDecoded
import java.io.File
import kotlinx.serialization.Serializable

val config: EngConfig by lazy {
    File(ENG_CONFIG_FILE).read().yamlDecoded()
}

@Serializable
class EngConfig(
    var engMicroPlatformUrl: String
)
