package ru.kosti.googledrivemanager.dto.user

data class CreateUserDto(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val repeatPassword: String
)
