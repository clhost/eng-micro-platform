package io.clhost.language.faults

abstract class BusinessLogicException(
    val errorCode: String,
    val readableMessage: String,
    val details: Map<String, String> = emptyMap()
) : RuntimeException("Business logic exception code=$errorCode, message: $readableMessage, details: $details")
