package io.clhost.tooling.eng

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import io.clhost.tooling.extensions.clockWaitingBar
import io.clhost.tooling.extensions.ensureNotNull
import io.clhost.tooling.extensions.green
import kotlinx.coroutines.runBlocking

class CreateWordCommand : CliktCommand(
    name = "create",
    help = "Create a definition for the new word"
) {

    private val word by option("-w", "--word")
        .help("A word for which should create a definition")
        .ensureNotNull()

    override fun run() = runBlocking {
        val deferred = engMicroPlatformClient.createWord(word)

        clockWaitingBar(
            delay = 500,
            line = "Creating a new word",
            exitCondition = { deferred.isCompleted }
        )

        echo("Done!\n".green)
        printWordDefinition(deferred.await())
    }
}

class GetWordCommand : CliktCommand(
    name = "get",
    help = "Get a definition for a word"
) {

    private val word by option("-w", "--word")
        .help("A word for which should find a definition")
        .ensureNotNull()

    override fun run() = runBlocking {
        val deferred = engMicroPlatformClient.getWord(word)

        clockWaitingBar(
            delay = 500,
            line = "Getting a new word",
            exitCondition = { deferred.isCompleted }
        )

        echo("Done!\n".green)
        printWordDefinition(deferred.await())
    }
}
