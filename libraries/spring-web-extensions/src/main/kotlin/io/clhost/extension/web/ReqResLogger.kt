package io.clhost.extension.web

import java.nio.charset.StandardCharsets
import org.slf4j.LoggerFactory
import org.springframework.web.util.ContentCachingResponseWrapper

class ReqResLogger {

    private val logger = LoggerFactory.getLogger(ReqResLogger::class.java)

    fun logRequest(request: ContentReadingRequestWrapper) {
        val queryString = request.queryString
        val message = """
            |HTTP Request
            |${request.method} ${request.requestURI} ${if (queryString != null) "?$queryString" else ""}
            |Headers: ${readHeaders(request)}
            |Body: ${String(request.getContentAsByteArray(), StandardCharsets.UTF_8)}
        """.trimMargin()
        logger.info(message)
    }

    fun logResponse(response: ContentCachingResponseWrapper) {
        val message = """
            |HTTP Response
            |HTTP Status: ${response.status}
            |Headers: ${readHeaders(response)}
            |Body: ${String(response.contentAsByteArray, StandardCharsets.UTF_8)}
        """.trimMargin()
        logger.info(message)
    }
}
