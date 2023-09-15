package io.clhost.extension.cli

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asDeferred

val httpClient: HttpClient = HttpClient
    .newBuilder()
    .followRedirects(HttpClient.Redirect.NORMAL)
    .connectTimeout(Duration.ofSeconds(30))
    .build()

val String.asBody: HttpRequest.BodyPublisher
    get() = HttpRequest.BodyPublishers.ofString(this)

fun HttpRequest.Builder.url(url: String) = apply { uri(URI.create(url)) }

fun HttpRequest.Builder.body(bodyPublisher: HttpRequest.BodyPublisher) = apply { POST(bodyPublisher) }

inline fun <reified R> HttpRequest.Builder.body(body: R) = apply {
    POST(HttpRequest.BodyPublishers.ofString(body.jsonEncoded()))
}

inline fun <reified T> HttpClient.get(block: (HttpRequest.Builder.() -> Unit) = {}): T {
    val request = HttpRequest.newBuilder()
        .GET()
        .apply(block)
        .build()
    return send(request, HttpResponse.BodyHandlers.ofString())
        .apply { ensureIs2xx() }
        .body()
        .jsonDecoded()
}

fun HttpClient.getRaw(block: (HttpRequest.Builder.() -> Unit) = {}): String {
    val request = HttpRequest.newBuilder()
        .GET()
        .apply(block)
        .build()
    return send(request, HttpResponse.BodyHandlers.ofString())
        .apply { ensureIs2xx() }
        .body()
}

inline fun <reified T> HttpClient.getAsync(block: (HttpRequest.Builder.() -> Unit) = {}): Deferred<T> {
    val request = HttpRequest.newBuilder()
        .GET()
        .apply(block)
        .build()
    return CoroutineScope(Dispatchers.IO).async {
        sendAsync(request, HttpResponse.BodyHandlers.ofString()).asDeferred()
            .await()
            .apply { ensureIs2xx() }
            .body()
            .jsonDecoded()
    }
}

inline fun <reified T> HttpClient.post(block: (HttpRequest.Builder.() -> Unit) = {}): T {
    val request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.noBody())
        .apply(block)
        .build()
    return send(request, HttpResponse.BodyHandlers.ofString())
        .apply { ensureIs2xx() }
        .body()
        .jsonDecoded()
}

inline fun <reified T> HttpClient.postAsync(block: (HttpRequest.Builder.() -> Unit) = {}): Deferred<T> {
    val request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.noBody())
        .apply(block)
        .build()
    return CoroutineScope(Dispatchers.IO).async {
        sendAsync(request, HttpResponse.BodyHandlers.ofString()).asDeferred()
            .await()
            .apply { ensureIs2xx() }
            .body()
            .jsonDecoded()
    }
}

inline fun <reified T> HttpClient.put(block: (HttpRequest.Builder.() -> Unit) = {}): T {
    val request = HttpRequest.newBuilder()
        .PUT(HttpRequest.BodyPublishers.noBody())
        .apply(block)
        .build()
    return send(request, HttpResponse.BodyHandlers.ofString())
        .apply { ensureIs2xx() }
        .body()
        .jsonDecoded()
}

class HttpRedirectionException(
    code: Int,
    message: String
) : RuntimeException("$code, $message") {
    init {
        require(code in 300..399) { "Status code must be between 300 and 399" }
    }
}

class HttpClientException(
    code: Int,
    message: String
) : RuntimeException("$code, $message") {
    init {
        require(code in 400..499) { "Status code must be between 400 and 499" }
    }
}

class HttpServerException(
    code: Int,
    message: String
) : RuntimeException("$code, $message") {
    init {
        require(code in 500..599) { "Status code must be between 500 and 599" }
    }
}

inline fun <reified T> HttpResponse<T>.ensureIs2xx() {
    val message = """
        HTTP request to ${request().uri()} failed
        Status code is ${statusCode()}
        Body: ${body()}
    """.trimIndent()

    when (statusCode()) {
        in 300..399 -> throw HttpRedirectionException(statusCode(), message)
        in 400..499 -> throw HttpClientException(statusCode(), message)
        in 500..599 -> throw HttpServerException(statusCode(), message)
    }
}
