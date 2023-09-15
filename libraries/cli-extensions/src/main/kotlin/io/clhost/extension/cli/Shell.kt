package io.clhost.extension.cli

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.ProcessBuilder.Redirect.INHERIT
import java.util.concurrent.TimeUnit

class Shell private constructor(
    val startDirectory: String
) {

    companion object {
        fun on(directory: String) = Shell(directory)
    }

    fun invokeSilent(command: List<String>) {
        val process = ProcessBuilder(*command.toTypedArray())
            .directory(File(startDirectory))
            .start()

        val reader = BufferedReader(InputStreamReader(process.inputStream))

        reader.forEveryLine { println(it) }

        process.apply { waitFor(60, TimeUnit.MINUTES) }
    }

    fun invoke(command: List<String>): Process {
        val process = ProcessBuilder(*command.toTypedArray())
            .start()

        val reader = BufferedReader(InputStreamReader(process.inputStream))

        reader.forEveryLine { println(it) }

        return process.apply { waitFor(60, TimeUnit.MINUTES) }
    }

    private fun BufferedReader.forEveryLine(block: (String) -> Unit) {
        while (true) {
            val line = readLine() ?: return
            block(line)
        }
    }
}
