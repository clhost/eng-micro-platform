package io.clhost.tooling.extensions

import com.github.ajalt.clikt.core.CliktError
import kotlin.system.exitProcess

class OptionMustBePresent(
    options: Set<String>
) : RuntimeException("Error: option $options requires a value")

class OneOfTheOptionsMustBePresent(
    options: Set<String>
) : RuntimeException("Error: one of the options $options must be present")

class OnlyOneOfTheOptionsMustBePresent(
    options: Set<String>
) : RuntimeException("Error: only one of the options $options must be present")

fun handleException(block: () -> Unit) = try {
    block()
} catch (ex: Exception) {
    if (ex is CliException || ex is CliktError) {
        println(ex.message.red)
    }

    if (ex !is CliException) {
        println("\nInternal error: ${ex.message}".red)
    }

    ex.printStackTrace()
    exitProcess(1)
}

abstract class CliException(message: String) : RuntimeException(message)
