package io.clhost.extension.ktor.client

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.apache.ApacheEngineConfig
import java.time.Duration

class Pool {
    class Config {
        var maxConnTotal: Int = 50
        var maxConnPerRoute: Int = 50
    }
}

class KeepAlive {
    class Config {
        var keepAlive: Duration = Duration.ofSeconds(30)
    }
}

fun HttpClientConfig<ApacheEngineConfig>.connectionPool(block: Pool.Config.() -> Unit) {
    val config = Pool.Config().apply(block)
    engine {
        customizeClient {
            setMaxConnTotal(config.maxConnTotal)
            setMaxConnPerRoute(config.maxConnPerRoute)
        }
    }
}

fun HttpClientConfig<ApacheEngineConfig>.keepALive(block: KeepAlive.Config.() -> Unit) {
    val config = KeepAlive.Config().apply(block)
    engine { customizeClient { setKeepAliveStrategy { _, _ -> config.keepAlive.seconds } } }
}
