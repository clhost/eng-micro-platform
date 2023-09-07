package io.clhost.extension.stdlib

import java.net.URLEncoder

inline fun <reified T> withResult(
    block: () -> T
): Result<T> =
    try {
        Result.success(block())
    } catch (ex: Exception) {
        Result.failure(ex)
    }

val String.encoded: String
    get() = URLEncoder.encode(this, "utf-8").replace("+", "%20")

inline fun <reified T : Throwable, R> ifExceptionCaught(value: R, block: () -> R) = try {
    block()
} catch (ex: Throwable) {
    if (ex is T || ex.cause is T) value else throw ex
}
