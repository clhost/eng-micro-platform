package io.clhost.starter.faults

import io.clhost.language.faults.ApiError
import io.clhost.language.faults.ConstraintViolation
import io.clhost.language.faults.invalidFormat
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError

fun apiErrorFromFieldErrorsOrFromGlobalError(
    fieldErrors: List<FieldError>,
    globalError: ObjectError?
): ApiError {
    if (fieldErrors.isNotEmpty()) {
        val details = fieldErrors
            .map { ConstraintViolation(it.field, it.defaultMessage ?: "Unknown error") }
            .sortedWith(compareBy({ it.field }, { it.description }))
        return ApiError("constraintViolations", "Fields are incorrect", details)
    }
    return invalidFormat(globalError?.defaultMessage)
}
