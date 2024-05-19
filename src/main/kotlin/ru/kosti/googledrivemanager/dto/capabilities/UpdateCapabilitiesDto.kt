package ru.kosti.googledrivemanager.dto.capabilities

import ru.kosti.googledrivemanager.dto.capabilities.PathDto
import java.util.UUID

data class UpdateCapabilitiesDto(
    val uuid: UUID,
    val title: String,
    val paths: List<PathDto>
)
