package io.clhost.platform.telegram.application

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.types.BotCommand
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

@Component
class BotApi(bot: TelegramBot) : BotLogic(
    listOf(
        BotCommand("a", "Test command named a"),
        BotCommand("b", "Test command named b")
    ),
    bot
) {

    @PostConstruct
    fun startup() = runBlocking { init() }

    override fun onJoin(): String {
        return "Joined"
    }

    override fun onText(text: String): String {
        return "Text"
    }

    override fun onCommand(command: String): String {
        return "Allowed command"
    }

    override fun onUnrecognizedContent(): String {
        return "Unrecognized content. Only text is possible here."
    }
}
