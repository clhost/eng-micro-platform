package io.clhost.extension.cli

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.fusesource.jansi.Ansi

fun clockWaitingBar(
    delay: Long,
    line: String,
    exitCondition: () -> Boolean
) = runBlocking {

    suspend fun renderWithDelay(render: () -> Unit) {
        render()
        delay(delay)
    }

    fun render(line: String) {
        print(Ansi.ansi().cursorUp(1).eraseLine(Ansi.Erase.FORWARD))
        println(line)
    }

    var i = 0
    val chain = listOf("(|)", "(/)", "(-)", "(\\)", "(|)", "(/)", "(-)", "(\\)")

    println()

    do {
        renderWithDelay { render("$line ${chain[i++]}...") }
        if (i == chain.size) i = 0
    } while (!exitCondition())
}
