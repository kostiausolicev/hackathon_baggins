package ru.kosti.googledrivemanager.dto.item

import ru.kosti.googledrivemanager.enumeration.MimeType

data class CreateItemDto(
    val name: String,
    val parent: String,
    val type: MimeType
)