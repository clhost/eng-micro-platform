package io.clhost.extension.stdlib

inline fun <reified T> withResult(
    block: () -> T
): Result<T> =
    try {
        Result.success(block())
    } catch (ex: Exception) {
        Result.failure(ex)
    }
