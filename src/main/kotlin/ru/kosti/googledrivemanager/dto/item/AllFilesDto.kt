package ru.kosti.googledrivemanager.dto.item

data class AllFilesDto(
    val nextPage: String? = null,
    val items: List<ItemDto>
)
