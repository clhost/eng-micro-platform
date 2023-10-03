package io.clhost.platform.telegram.application

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onChatJoinRequest
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onContentMessage
import dev.inmo.tgbotapi.extensions.utils.textContentOrNull
import dev.inmo.tgbotapi.types.BotCommand
import org.slf4j.LoggerFactory

abstract class BotLogic(
    private val commands: List<BotCommand>,
    private val bot: TelegramBot
) {

    private val logger = LoggerFactory.getLogger(BotLogic::class.java)

    suspend fun init() = bot.buildBehaviourWithLongPolling {
        onChatJoinRequest {
            logger.info("Received chatJoinRequest event from ${it.userChatId}")
            send(it.chat, onJoin())
        }
        onContentMessage {
            val text = it.content.textContentOrNull()?.text

            if (text == null) {
                logger.info("Received unrecognized content from ${it.chat.id}")
                send(it.chat, onUnrecognizedContent())
                return@onContentMessage
            }

            if (text.startsWith("/") && commands.none { command -> "/${command.command}" == text }) {
                logger.info("Received unrecognized command $text from ${it.chat.id}")
                send(it.chat, "Unrecognized command. Say what?")
                return@onContentMessage
            }

            if (text.startsWith("/")) {
                logger.info("Received command $text from ${it.chat.id}")
                send(it.chat, onCommand(text))
                return@onContentMessage
            }

            logger.info("Received content \"$text\" from ${it.chat.id}")
            send(it.chat, onText(text))
        }
        setMyCommands(commands.map { BotCommand(it.command, it.description) })
    }

    abstract fun onJoin(): String

    abstract fun onText(text: String): String

    abstract fun onCommand(command: String): String

    abstract fun onUnrecognizedContent(): String
}
