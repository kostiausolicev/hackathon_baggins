package ru.kosti.googledrivemanager.configuration

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ru.kosti.googledrivemanager.exception.ApiException

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun exceptionHandler(ex: Exception) =
        ResponseEntity<String>(ex.message, HttpStatus.INTERNAL_SERVER_ERROR)

    @ExceptionHandler(ApiException::class)
    fun apiExceptionHandler(ex: ApiException) =
        ResponseEntity<String>(ex.message, ex.code)
}