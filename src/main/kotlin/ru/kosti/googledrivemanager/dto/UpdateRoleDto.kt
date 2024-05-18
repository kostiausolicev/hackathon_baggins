package ru.kosti.googledrivemanager.dto

import java.util.UUID

data class UpdateRoleDto(
    val uuid: UUID,
    val title: String,
    val paths: List<PathDto>
)
