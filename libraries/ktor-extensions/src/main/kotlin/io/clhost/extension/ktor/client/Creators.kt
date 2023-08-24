package io.clhost.extension.ktor.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.apache.Apache
import io.ktor.client.engine.apache.ApacheEngineConfig

fun createKtorClient(
    block: HttpClientConfig<ApacheEngineConfig>.() -> Unit = {}
) = HttpClient(Apache) {
    block()
    expectSuccess = true
}
