package io.clhost.extension.cli

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ParameterHolder
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.options.NullableOption
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.github.ajalt.clikt.parameters.options.eagerOption
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.transformAll

fun <EachT : Any, ValueT> NullableOption<EachT, ValueT>.ensureNotNull(): OptionWithValues<EachT, EachT, ValueT> {
    return transformAll { it.lastOrNull() ?: throw OptionMustBePresent(names) }
}

fun ParameterHolder.option(
    vararg names: String,
    help: String = "",
    hidden: Boolean = false,
    envvar: String? = null,
    helpTags: Map<String, String> = emptyMap(),
    completionCandidates: CompletionCandidates? = null,
    valueSourceKey: String? = null,
) = option(
    names = names,
    help = help,
    metavar = "",
    hidden = hidden,
    envvar = envvar,
    helpTags = helpTags,
    completionCandidates = completionCandidates,
    valueSourceKey = valueSourceKey,
)

inline fun <T : CliktCommand> T.versionOption(
    version: String,
    help: String = "Show the version and exit",
    names: Set<String> = setOf("-v", "--version"),
    crossinline message: (String) -> String = { "version is $it" },
): T = eagerOption(names, help) { throw PrintMessage(message(version)) }

fun throwMessage(message: String): Nothing = throw PrintMessage(message)
