package io.clhost.platform.telegram.infrastructure

import dev.inmo.tgbotapi.bot.ktor.telegramBot
import io.ktor.client.HttpClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BotConfiguration(
    @Value("\${telegram-bot-token}")
    private val botToken: String,

    @Qualifier("telegramBotKtorClient")
    private val ktorClient: HttpClient
) {

    @Bean
    fun bot() = telegramBot(botToken) { client = ktorClient }
}
