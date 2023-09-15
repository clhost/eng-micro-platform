package io.clhost.extension.cli

import java.io.File
import java.net.URLEncoder

val String.encoded: String
    get() = URLEncoder.encode(this, "utf-8").replace("+", "%20")

fun String.ifNotEmpty(block: (String) -> Unit) {
    if (isNotEmpty()) {
        block(this)
    }
}

fun Boolean.ifFalse(block: () -> Unit) {
    if (!this) {
        block()
    }
}

fun File.createIfNotExists() = apply { createNewFile() }

fun File.read() = readText().trim()

fun File.readValue(key: String) = read()
    .lines()
    .filter { it.contains(key) }
    .associate { it.split("=")[0] to it.split("=")[1] }[key]

inline fun <reified T : Throwable, R> ifExceptionCaught(value: R, block: () -> R) = try {
    block()
} catch (ex: Throwable) {
    if (ex is T || ex.cause is T) value else throw ex
}

inline fun <reified T : Throwable> falseIfExceptionCaught(block: () -> Boolean) = try {
    block()
} catch (ex: Throwable) {
    if (ex is T || ex.cause is T) false else throw ex
}

inline fun <T, R> Iterable<T>.partitionedSequentialMap(partitionSize: Int, transform: (List<T>) -> List<R>): List<R> {
    return chunked(partitionSize).flatMap(transform)
}
