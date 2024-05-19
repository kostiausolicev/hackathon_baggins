package ru.kosti.googledrivemanager.dto.user

import ru.kosti.googledrivemanager.enumeration.Roles
import java.util.*

data class UpdateUserDto(
    val uuid: UUID,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val password: String? = null,
    val capabilities: UUID? = null,
    val role: Roles? = null,
    val repeatPassword: String? = null
)
