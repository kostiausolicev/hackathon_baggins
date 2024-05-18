package ru.kosti.googledrivemanager.dto

import java.util.*

data class RoleDto(
    val uuid: UUID,
    val title: String,
    val paths: List<PathDto>
)

data class PathDto(
    val name: String,
    val id: String
)
