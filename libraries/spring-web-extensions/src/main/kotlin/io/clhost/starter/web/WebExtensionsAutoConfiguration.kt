package io.clhost.starter.web

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import io.clhost.extension.web.CorrelatedLoggingFilter

@Configuration
@ConditionalOnClass(RequestMappingHandlerMapping::class)
open class WebExtensionsAutoConfiguration {

    @Bean
    @ConditionalOnProperty(
        value = ["spring-web-extensions.logging.enabled"],
        havingValue = "true",
        matchIfMissing = true
    )
    open fun correlatedLoggingFilter() = CorrelatedLoggingFilter()
}
