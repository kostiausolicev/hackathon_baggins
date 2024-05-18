package ru.kosti.googledrivemanager.dto

import java.util.UUID

data class CreateUserDto(
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: UUID
)
