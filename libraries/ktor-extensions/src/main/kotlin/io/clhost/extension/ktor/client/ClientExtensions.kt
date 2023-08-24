package io.clhost.extension.ktor.client

import io.ktor.http.ContentType
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.contentType

fun HttpMessageBuilder.applicationJson() = contentType(ContentType.Application.Json)
