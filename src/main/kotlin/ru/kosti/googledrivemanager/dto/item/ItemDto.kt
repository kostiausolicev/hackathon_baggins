package ru.kosti.googledrivemanager.dto.item

import ru.kosti.googledrivemanager.enumeration.MimeType

data class ItemDto(
    val id: String,
    val name: String,
    val type: MimeType
)