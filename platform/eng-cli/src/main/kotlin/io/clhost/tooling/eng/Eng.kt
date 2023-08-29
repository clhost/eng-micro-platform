package io.clhost.tooling.eng

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import io.clhost.language.eng.CreateWordDefinitionCommand
import io.clhost.language.eng.Meaning
import io.clhost.language.eng.Pronunciation
import io.clhost.language.eng.Translation
import io.clhost.language.eng.WordDefinitionDto
import io.clhost.tooling.extensions.handleException
import io.clhost.tooling.extensions.module
import io.clhost.tooling.extensions.read
import io.clhost.tooling.extensions.versionOption
import java.io.File
import kotlinx.serialization.modules.SerializersModule

val HOME = System.getenv()["HOME"]

val ENG_HOME_DIR = "$HOME/.eng"
val ENG_CONFIG_FILE = "$ENG_HOME_DIR/config"
val ENG_VERSION_FILE = "$ENG_HOME_DIR/version"

class Eng : CliktCommand(printHelpOnEmptyArgs = true) {

    init {
        completionOption(hidden = true)
        versionOption(File(ENG_VERSION_FILE).read().replace("\n", ""))
        subcommands(
            CreateWordCommand(),
            GetWordCommand()
        )
    }

    override fun run() {}
}

fun main(args: Array<String>) = handleException {
    module = {
        SerializersModule {
            contextual(EngConfig::class, EngConfig.serializer())
            contextual(CreateWordDefinitionCommand::class, CreateWordDefinitionCommand.serializer())
            contextual(WordDefinitionDto::class, WordDefinitionDto.serializer())
            contextual(Translation::class, Translation.serializer())
            contextual(Meaning::class, Meaning.serializer())
            contextual(Pronunciation::class, Pronunciation.serializer())
        }
    }
    Eng().main(args)
}
