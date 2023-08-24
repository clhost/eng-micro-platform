package io.clhost.extension.ktor.client

import com.github.tomakehurst.wiremock.WireMockServer
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.AutoScan

@AutoScan
object WireMockCreator : ProjectListener {

    private val server = WireMockServer(8080)

    override suspend fun beforeProject() {
        super.beforeProject()
        server.start()
    }

    override suspend fun afterProject() {
        super.afterProject()
        server.stop()
    }
}

data class TestData(
    val data: String
)
