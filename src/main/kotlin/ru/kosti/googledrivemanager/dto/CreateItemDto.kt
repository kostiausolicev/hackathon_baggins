package ru.kosti.googledrivemanager.dto

import ru.kosti.googledrivemanager.enumeration.MimeType

data class CreateItemDto(
    val name: String,
    val parent: String,
    val type: MimeType
)