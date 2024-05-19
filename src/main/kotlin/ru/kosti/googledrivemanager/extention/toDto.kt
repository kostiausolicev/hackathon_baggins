package ru.kosti.googledrivemanager.extention

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import ru.kosti.googledrivemanager.dto.item.AllFilesDto
import ru.kosti.googledrivemanager.dto.item.ItemDto
import ru.kosti.googledrivemanager.dto.capabilities.PathDto
import ru.kosti.googledrivemanager.dto.capabilities.CapabilitiesDto
import ru.kosti.googledrivemanager.dto.user.UserDto
import ru.kosti.googledrivemanager.entity.CapabilitiesEntity
import ru.kosti.googledrivemanager.entity.UserEntity
import ru.kosti.googledrivemanager.enumeration.MimeType


fun FileList.toDto(): AllFilesDto {
    val items = this.files.map { it.toDto() }
    return AllFilesDto(
        nextPage = nextPageToken,
        items = items
    )
}

fun Set<String>.toDto(drive: Drive): AllFilesDto {
    val items = this.map {
        val file = drive.files().get(it).setFields("id, name, mimeType").execute()
        ItemDto(
            id = it,
            name = file.name,
            type = MimeType.findByGoogleName(file.mimeType)
        )
    }
    return AllFilesDto(
        nextPage = null,
        items = items
    )
}

fun File.toDto(): ItemDto =
    ItemDto(
        id = this.id,
        name = this.name,
        type = MimeType.findByGoogleName(this.mimeType)
    )

fun CapabilitiesEntity.toDto(drive: Drive) =
    CapabilitiesDto(
        uuid = uuid,
        title = title,
        paths = paths.map { pathId ->
            val file = drive.files().get(pathId).setFields("id, name").execute()
            val name = file.name
            PathDto(name = name, id = pathId)
        }
    )

fun UserEntity.toDto(drive: Drive) =
    UserDto(
        uuid = this.uuid,
        lastName = this.lastName,
        firstName = this.firstName,
        isConform = this.isConformed,
        email = this.email,
        roles = this.role,
        capabilities = this.capabilities?.toDto(drive)
    )
