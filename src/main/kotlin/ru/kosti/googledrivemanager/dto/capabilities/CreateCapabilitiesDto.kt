package ru.kosti.googledrivemanager.dto.capabilities

data class CreateCapabilitiesDto(
    val title: String,
    val paths: List<PathDto>
)
