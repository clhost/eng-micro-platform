package io.clhost.tooling.engp

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import io.clhost.extension.cli.handleException
import io.clhost.extension.cli.read
import io.clhost.extension.cli.versionOption
import java.io.File

val HOME = System.getenv()["HOME"]

val ENGP_HOME_DIR = "$HOME/.engp"
val ENGP_SOURCE_DIR = "$ENGP_HOME_DIR/src"
val ENGP_VERSION_FILE = "$ENGP_HOME_DIR/version"

val ENGP_APP_VERSIONS_FILE = "$ENGP_SOURCE_DIR/versions.yml"

class Engp : CliktCommand(printHelpOnEmptyArgs = true) {

    init {
        completionOption(hidden = true)
        versionOption(File(ENGP_VERSION_FILE).read().replace("\n", ""))
        subcommands(StartupCommand(), ShutdownCommand(), AppCommand(), EnvCommand(), RequirementsCommand())
    }

    override fun run() {}
}

fun main(args: Array<String>) = handleException {
    Engp().main(args)
}
