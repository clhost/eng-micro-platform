package io.clhost.language.faults

import com.fasterxml.jackson.annotation.JsonInclude

data class ApiError(
    val errorCode: String,
    val description: String,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val constraintViolations: List<ConstraintViolation> = listOf(),
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val responseBody: Map<String, String> = emptyMap()
)

data class ConstraintViolation(
    val field: String,
    val description: String
)

fun internalError(description: String?) = ApiError(
    "internalError",
    description ?: "Unknown error"
)

fun invalidFormat(description: String?) = ApiError(
    "invalidFormat",
    description ?: "Unknown error"
)
