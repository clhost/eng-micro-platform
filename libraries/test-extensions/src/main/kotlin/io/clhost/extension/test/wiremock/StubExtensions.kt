package io.clhost.extension.test.wiremock

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.matching.UrlPattern

fun stubGet(urlPattern: UrlPattern, status: Int = 200, definition: ResponseDefinitionBuilder.() -> String) {
    val response = aResponse()
    val body = definition(response)
    stubFor(
        get(urlPattern)
            .willReturn(
                response
                    .withStatus(status)
                    .withBody(body)
            )
    )
}

fun stubPost(urlPattern: UrlPattern, status: Int = 200, definition: ResponseDefinitionBuilder.() -> String) {
    val response = aResponse()
    val body = definition(response)
    stubFor(
        post(urlPattern)
            .willReturn(
                response
                    .withStatus(status)
                    .withBody(body)
            )
    )
}

fun stubJsonGet(url: String, status: Int = 200, body: () -> String) {
    stubFor(
        get(urlEqualTo(url))
            .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", "application/json")
                    .withBody(body())
            )
    )
}

fun stubJsonGet(urlPattern: UrlPattern, status: Int = 200, body: () -> String) {
    stubFor(
        get(urlPattern)
            .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", "application/json")
                    .withBody(body())
            )
    )
}

fun stubJsonPost(url: String, status: Int = 200, body: () -> String) {
    stubFor(
        post(urlEqualTo(url))
            .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", "application/json")
                    .withBody(body())
            )
    )
}

fun stubJsonPost(urlPattern: UrlPattern, status: Int = 200, body: () -> String) {
    stubFor(
        post(urlPattern)
            .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", "application/json")
                    .withBody(body())
            )
    )
}
