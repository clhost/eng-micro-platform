package io.clhost.extension.ktor.client

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext

suspend fun <T> withPreservedCorrelationId(
    context: CoroutineContext = Dispatchers.IO,
    block: suspend CoroutineScope.() -> T
) = withContext(CoroutineScope(context + MDCContext()).coroutineContext, block)

fun <T> runBlockingWithPreservedCorrelationId(
    context: CoroutineContext = Dispatchers.IO,
    block: suspend CoroutineScope.() -> T
) = runBlocking { withContext(CoroutineScope(context + MDCContext()).coroutineContext, block) }
