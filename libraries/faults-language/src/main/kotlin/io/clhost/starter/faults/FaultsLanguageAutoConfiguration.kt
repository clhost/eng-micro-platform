package io.clhost.starter.faults

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["faults-language.handlers.enabled"],
    havingValue = "true",
    matchIfMissing = true
)
open class FaultsLanguageAutoConfiguration {

    @Bean
    open fun webExceptionHandler() = ExceptionHandler()
}
