package ru.kosti.googledrivemanager.service

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.api.services.drive.model.Permission
import com.google.api.services.drive.model.PermissionList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.dto.AllFilesDto
import ru.kosti.googledrivemanager.dto.CreateItemDto
import ru.kosti.googledrivemanager.dto.ItemDto
import ru.kosti.googledrivemanager.enumeration.MimeType

@Service
class DriveService(
    private val drive: Drive
) {
    private val semaphore = Semaphore(500)

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

    suspend fun create(newFile: CreateItemDto) {
        val file = File().apply {
            name = newFile.name
            parents = listOf(newFile.parent)
            mimeType = newFile.type.googleName
            kind = "drive#file"
        }
        val fileId = drive.files().create(file)
            .execute()
        coroutineScope {
            // TODO Получить пользователей и дать им права на редактирование
            val email = "kostiausolicev@gmail.com"
            semaphore.withPermit {
                drive.Permissions().create(fileId.id, Permission().apply {
                    type = "user"
                    emailAddress = email
                    role = "writer"
                }
                ).execute()
            }
        }
    }

    suspend fun removeAccess(userEmail: String) =
        CoroutineScope(Dispatchers.Default).launch {
            val query = "'$userEmail' in writers or '$userEmail' in owners"
            val result = drive.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute()
                .files.map { it.id }
            launch {
                result.forEach { fileId ->
                    launch {
                        semaphore.withPermit {
                            val permissions: PermissionList = drive.permissions()
                                .list(fileId)
                                .setFields("permissions(id, emailAddress)")
                                .execute()
                            val permissionId = permissions.permissions.find { it.emailAddress == userEmail }?.id
                            permissionId?.let {
                                drive.permissions().delete(fileId, it).execute()
                            }
                        }
                    }
                }
            }
        }

    suspend fun addAccess(userEmail: String, rootFolderId: String) = CoroutineScope(Dispatchers.Default).launch {
        val foldersToProcess = ArrayDeque<String>()
        foldersToProcess.add(rootFolderId)

        while (foldersToProcess.isNotEmpty()) {
            val currentFolderId = foldersToProcess.removeFirst()
            val result: List<File> = drive.files().list()
                .setQ("'$currentFolderId' in parents and trashed=false")
                .setFields("files(id, name, mimeType)")
                .execute()
                .files
            launch {
                result.forEach { file: File ->
                    launch {
                        semaphore.withPermit {
                            addEditPermission(userEmail, file.id)
                            if (file.mimeType == MimeType.FOLDER.googleName) {
                                foldersToProcess.addLast(file.id)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addEditPermission(userEmail: String, fileId: String) {
        val permission = Permission().apply {
            type = "user"
            role = "writer"
            emailAddress = userEmail
        }
        drive.permissions().create(fileId, permission).execute()
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