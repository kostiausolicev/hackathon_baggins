package ru.kosti.googledrivemanager.dto.user

import ru.kosti.googledrivemanager.enumeration.Roles

data class SuccessAuthDto(
    val token: String,
    val role: Roles
)