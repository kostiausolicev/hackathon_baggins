package ru.kosti.googledrivemanager.dto.capabilities

import java.util.*

data class CapabilitiesDto(
    val uuid: UUID,
    val title: String,
    val paths: List<PathDto>
)
