package ru.kosti.googledrivemanager.dto.user

import ru.kosti.googledrivemanager.dto.capabilities.CapabilitiesDto
import ru.kosti.googledrivemanager.enumeration.Roles
import java.util.*

data class UserDto(
    val uuid: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isConform: Boolean,
    val roles: Roles,
    val capabilities: CapabilitiesDto?
)
