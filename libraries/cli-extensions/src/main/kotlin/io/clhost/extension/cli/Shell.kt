package io.clhost.extension.cli

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class Shell private constructor(
    val startDirectory: String
) {

    companion object {
        fun on(directory: String) = Shell(directory)
    }

    fun invokeSilentWithPrint(command: List<String>) {
        val process = ProcessBuilder(*command.toTypedArray())
            .directory(File(startDirectory))
            .start()

        val reader = BufferedReader(InputStreamReader(process.inputStream))

        process.apply { waitFor(5, TimeUnit.MINUTES) }

        reader.forEveryLine { println(it) }
    }

    fun invokeWithPrint(command: List<String>): Process {
        val process = ProcessBuilder(*command.toTypedArray())
            .start()
            .apply { waitFor(5, TimeUnit.MINUTES) }

        val reader = BufferedReader(InputStreamReader(process.inputStream))

        reader.forEveryLine { println(it) }

        return process
    }

    fun invoke(command: List<String>): Process {
        val process = ProcessBuilder(*command.toTypedArray())
            .start()
        return process.apply { waitFor(5, TimeUnit.MINUTES) }
    }
}

private fun BufferedReader.forEveryLine(block: (String) -> Unit) {
    while (true) {
        val line = readLine() ?: return
        block(line)
    }
}

val Process.asString: String
    get() = inputStream
        .use { input -> BufferedInputStream(input).readAllBytes() }
        .toString(StandardCharsets.UTF_8)
        .trim()
