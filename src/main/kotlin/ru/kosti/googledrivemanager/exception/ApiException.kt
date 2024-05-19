package ru.kosti.googledrivemanager.exception

import org.springframework.http.HttpStatusCode

class ApiException(
    val code: HttpStatusCode,
    message: String? = null
): RuntimeException(message)