package io.clhost.tooling.extensions

import java.io.BufferedInputStream
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class Shell private constructor(
    val startDirectory: String
) {

    companion object {
        fun on(directory: String) = Shell(directory)
    }

    fun invoke(command: String, redirectOutput: ProcessBuilder.Redirect? = null): Process {
        val parts = command.split("\\s".toRegex())
        return invoke(parts, redirectOutput)
    }

    fun invoke(command: List<String>, redirectOutput: ProcessBuilder.Redirect? = null): Process {
        val process = ProcessBuilder(*command.toTypedArray())
            .directory(File(startDirectory))
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .apply { if (redirectOutput != null) redirectOutput(redirectOutput) }
            .start()
        return process.apply { waitFor(60, TimeUnit.MINUTES) }
    }
}

fun Shell.platform(): String {
    val platform = invoke("uname").asString
    return if ("darwin".equals(platform, ignoreCase = true)) "macos" else "linux"
}

fun Shell.mkdirs(subDirectory: String) {
    val directory = "$startDirectory/$subDirectory"
    invoke("mkdir -p $directory", ProcessBuilder.Redirect.INHERIT)
    invoke("echo Created directory $directory", ProcessBuilder.Redirect.INHERIT)
}

fun Shell.sed(sed: String, file: String) = when (platform()) {
    "macos" -> invoke("sed -r -i '' $sed $file")
    "linux" -> invoke("sed -r -i $sed $file")
    else -> throw IllegalArgumentException(platform())
}

fun Shell.unzip(archive: String, destination: String) {
    invoke("unzip -o $archive -d $destination")
}

val Process.asString: String
    get() = inputStream
        .use { input -> BufferedInputStream(input).readAllBytes() }
        .toString(StandardCharsets.UTF_8)
        .trim()
