package io.clhost.tooling.eng

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import io.clhost.extension.cli.OneOfTheOptionsMustBePresent
import io.clhost.extension.cli.OnlyOneOfTheOptionsMustBePresent
import io.clhost.extension.cli.Shell
import io.clhost.extension.cli.clockWaitingBar
import io.clhost.extension.cli.green
import kotlinx.coroutines.runBlocking

class UseMinikubeCommand : CliktCommand(
    name = "use-minikube",
    help = "Use minikube eng-micro-platform url"
) {
    override fun run() = echo(config.useMinikubeIpForEngMicroPlatformUrl(Shell.on(HOME).minikubeIp()).encoded())
}

class UseLocalCommand : CliktCommand(
    name = "use-local",
    help = "Use local eng-micro-platform url"
) {
    override fun run() = echo(config.useLocalEngMicroPlatformUrl().encoded())
}

class CreateCommand : CliktCommand(
    name = "create",
    help = "Create a definition for the new word or for the new phrase"
) {

    private val word by option("-w", "--word")
        .help("A word for which should create a definition")

    private val phrase by option("-p", "--phrase")
        .help("A phrase for which should create a definition")

    override fun run() = runBlocking {
        ensureOnlyWordOrPhraseIsPresent(word, phrase)

        if (word != null && phrase == null) {
            val deferred = engMicroPlatformClient.createWord(word!!)

            clockWaitingBar(
                delay = 500,
                line = "Creating a new word",
                exitCondition = { deferred.isCompleted }
            )

            echo("Done!\n".green)
            printWordDefinition(deferred.await())
        }

        if (phrase != null && word == null) {
            val deferred = engMicroPlatformClient.createPhrase(phrase!!)

            clockWaitingBar(
                delay = 500,
                line = "Creating a new phrase",
                exitCondition = { deferred.isCompleted }
            )

            echo("Done!\n".green)
            printPhraseDefinition(deferred.await())
        }
    }
}

class GetCommand : CliktCommand(
    name = "get",
    help = "Get the definition for the word or for the phrase"
) {

    private val word by option("-w", "--word")
        .help("A word for which should find a definition")

    private val phrase by option("-p", "--phrase")
        .help("A phrase for which should create a definition")

    override fun run() = runBlocking {
        ensureOnlyWordOrPhraseIsPresent(word, phrase)

        if (word != null && phrase == null) {
            val deferred = engMicroPlatformClient.getWord(word!!)

            clockWaitingBar(
                delay = 500,
                line = "Getting the word",
                exitCondition = { deferred.isCompleted }
            )

            echo("Done!\n".green)
            printWordDefinition(deferred.await())
        }

        if (phrase != null && word == null) {
            val deferred = engMicroPlatformClient.getPhrase(phrase!!)

            clockWaitingBar(
                delay = 500,
                line = "Getting the phrase",
                exitCondition = { deferred.isCompleted }
            )

            echo("Done!\n".green)
            printPhraseDefinition(deferred.await())
        }
    }
}

class DeleteCommand : CliktCommand(
    name = "delete",
    help = "Delete the definition for the word or for the phrase"
) {

    private val word by option("-w", "--word")
        .help("A word for which should delete the definition")

    private val phrase by option("-p", "--phrase")
        .help("A phrase for which should delete the definition")

    override fun run() = runBlocking {
        ensureOnlyWordOrPhraseIsPresent(word, phrase)

        if (word != null && phrase == null) {
            val deferred = engMicroPlatformClient.deleteWord(word!!)

            clockWaitingBar(
                delay = 500,
                line = "Deleting the word",
                exitCondition = { deferred.isCompleted }
            )

            echo("Done!\n".green)
        }

        if (phrase != null && word == null) {
            val deferred = engMicroPlatformClient.deletePhrase(phrase!!)

            clockWaitingBar(
                delay = 500,
                line = "Deleting the phrase",
                exitCondition = { deferred.isCompleted }
            )

            echo("Done!\n".green)
        }
    }
}

internal fun ensureOnlyWordOrPhraseIsPresent(word: String?, phrase: String?) {
    if (word == null && phrase == null) throw OneOfTheOptionsMustBePresent(setOf("word", "phrase"))
    if (word != null && phrase != null) throw OnlyOneOfTheOptionsMustBePresent(setOf("word", "phrase"))
}
