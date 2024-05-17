package ru.kosti.googledrivemanager.dto

data class AllFilesDto(
    val nextPage: String? = null,
    val items: List<ItemDto>
)
