package io.clhost.tooling.eng

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import io.clhost.tooling.extensions.handleException
import io.clhost.tooling.extensions.module
import kotlinx.serialization.modules.SerializersModule

val HOME = System.getenv()["HOME"]

val ENG_HOME_DIR = "$HOME/.eng"
val ENG_SRC_DIR = "$ENG_HOME_DIR/src"
val ENG_BIN_DIR = "$ENG_HOME_DIR/bin"
val ENG_TMP_DIR = "$ENG_HOME_DIR/tmp"
val ENG_CACHE_DIR = "$ENG_HOME_DIR/cache"
val ENG_CONFIG_DIR = "$ENG_HOME_DIR/config"

val ENG_FILE = "$ENG_BIN_DIR/eng"
val ENG_ZIP_FILE = "$ENG_TMP_DIR/eng.zip"
val ENG_VERSION_FILE = "$ENG_CACHE_DIR/version"
val ENG_CONFIG_FILE = "$ENG_CONFIG_DIR/config"
val ENG_SOURCES_ZIP_FILE = "$ENG_TMP_DIR/sources.zip"

val ENG_BASH_COMPLETIONS_FILE = "$ENG_SRC_DIR/eng-bash-completions.sh"
val ENG_ZSH_COMPLETIONS_FILE = "$ENG_SRC_DIR/eng-zsh-completions.sh"
val ENG_FISH_COMPLETIONS_FILE = "$ENG_SRC_DIR/eng-completions.fish"

class Eng : CliktCommand(printHelpOnEmptyArgs = true) {

    init {
        completionOption(hidden = true)
        // versionOption(File(ENG_VERSION_FILE).read().replace("\n", ""))
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
            contextual(CreateWordDefinition::class, CreateWordDefinition.serializer())
            contextual(WordDefinition::class, WordDefinition.serializer())
            contextual(Example::class, Example.serializer())
            contextual(Translation::class, Translation.serializer())
            contextual(Meaning::class, Meaning.serializer())
            contextual(Pronunciation::class, Pronunciation.serializer())
        }
    }
    Eng().main(args)
}
