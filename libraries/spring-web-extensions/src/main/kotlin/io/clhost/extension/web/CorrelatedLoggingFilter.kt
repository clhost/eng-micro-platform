package io.clhost.extension.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.util.ContentCachingResponseWrapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
open class CorrelatedLoggingFilter : OncePerRequestFilter() {

    @Autowired
    lateinit var requestMappingHandlerMapping: RequestMappingHandlerMapping

    private val reqResLogger = ReqResLogger()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestWrapper = ContentReadingRequestWrapper(request)

        val responseWrapper =
            if (response is ContentCachingResponseWrapper) response
            else ContentCachingResponseWrapper(response)

        try {
            val correlationId = request.getHeader(CorrelationId.headerName)
                ?: request.getAttribute(CorrelationId.headerName) as String?
                ?: CorrelationId.generate()

            request.setAttribute(CorrelationId.headerName, correlationId)

            CorrelationId.save(correlationId)

            if (request.shouldBeLogged) {
                reqResLogger.logRequest(requestWrapper)
            }

            filterChain.doFilter(requestWrapper, responseWrapper)
        } finally {
            response.setHeader(CorrelationId.headerName, CorrelationId.get())

            if (request.shouldBeLogged) {
                reqResLogger.logResponse(responseWrapper)
            }

            CorrelationId.remove()

            if (!requestWrapper.isAsyncStarted) {
                responseWrapper.copyBodyToResponse()
            }
        }
    }

    private val HttpServletRequest.shouldBeLogged: Boolean
        get() = try {
            requestMappingHandlerMapping.getHandler(this) != null
        } catch (ex: Exception) {
            true
        }
}
