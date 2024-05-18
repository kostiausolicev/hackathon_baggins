package ru.kosti.googledrivemanager.extention

import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import ru.kosti.googledrivemanager.dto.AllFilesDto
import ru.kosti.googledrivemanager.dto.ItemDto
import ru.kosti.googledrivemanager.enumeration.MimeType


fun FileList.toDto(): AllFilesDto {
    val items = this.files.map { it.toDto() }
    return AllFilesDto(
        nextPage = nextPageToken,
        items = items
    )
}

fun File.toDto(): ItemDto =
    ItemDto(
        id = this.id,
        name = this.name,
        type = MimeType.findByGoogleName(this.mimeType)
    )