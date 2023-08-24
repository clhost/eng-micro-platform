package io.clhost.starter.faults

import io.clhost.language.faults.ApiError
import io.clhost.language.faults.BusinessLogicException
import io.clhost.language.faults.internalError
import io.clhost.language.faults.invalidFormat
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import jakarta.validation.ConstraintViolationException

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ExceptionHandler {

    private val logger = LoggerFactory.getLogger(ExceptionHandler::class.java)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<Any> {
        logger.info("Handled HttpMessageNotReadableException: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(invalidFormat("Http message not readable"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<Any> {
        logger.info("Handled MethodArgumentNotValidException: {}", ex.message)
        val apiError = apiErrorFromFieldErrorsOrFromGlobalError(
            fieldErrors = ex.bindingResult.fieldErrors,
            globalError = ex.bindingResult.globalError
        )
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(apiError)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleEmptyResultDataAccess(ex: ConstraintViolationException): ResponseEntity<Any> {
        logger.info("Handled ConstraintViolationException: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(internalError("Internal constraint violation: ${ex.message}"))
    }

    @ExceptionHandler(BusinessLogicException::class)
    fun handleBusinessLogicError(ex: BusinessLogicException): ResponseEntity<Any> {
        logger.info("Handled BusinessLogicException: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiError(ex.errorCode, ex.readableMessage, responseBody = ex.details))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentTypeMismatchException): ResponseEntity<Any> {
        logger.info("Handled MethodArgumentTypeMismatchException - {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(invalidFormat(ex.localizedMessage))
    }

    @ExceptionHandler(Exception::class)
    fun handleAnyException(ex: Exception): ResponseEntity<Any> {
        logger.error("Handled exception", ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(internalError("Internal error: ${ex.message}"))
    }
}
