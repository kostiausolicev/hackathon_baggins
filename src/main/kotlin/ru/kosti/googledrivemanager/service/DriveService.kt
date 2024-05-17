package ru.kosti.googledrivemanager.service

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.dto.AllFilesDto
import ru.kosti.googledrivemanager.dto.CreateItemDto
import ru.kosti.googledrivemanager.dto.ItemDto
import ru.kosti.googledrivemanager.enumeration.MimeType

@Service
class DriveService(
    private val drive: Drive
) {
    fun getAll(
        limit: Int,
        root: String = "1OGPa_sQSfshN8-NspxHJtagj47-0ZzEn",
        pageToken: String? = null
    ): AllFilesDto {
        val folderId = root ?: "root"
        val query = "'$folderId' in parents"

        val result: FileList = drive.files().list()
            .setQ(query)
            .setSpaces("drive")
            .setPageSize(limit)
            .setFields("nextPageToken, files(id, name, mimeType)")
            .setPageToken(pageToken)
            .execute()
        return result.toDto()
    }

    fun create(newFile: CreateItemDto) {
        val file = File().apply {
            name = newFile.name
            parents = listOf(newFile.parent)
            mimeType = newFile.type.googleName
            kind = "drive#file"
        }
        drive.files().create(file)
            .execute()
    }

    private fun FileList.toDto(): AllFilesDto {
        val items = this.files.map { it.toDto() }
        return AllFilesDto(
            nextPage = nextPageToken,
            items = items
        )
    }

    private fun File.toDto(): ItemDto =
        ItemDto(
            id = this.id,
            name = this.name,
            type = MimeType.findByGoogleName(this.mimeType)
        )
}