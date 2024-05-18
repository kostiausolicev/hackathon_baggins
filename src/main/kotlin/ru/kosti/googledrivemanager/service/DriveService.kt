package ru.kosti.googledrivemanager.service

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.api.services.drive.model.Permission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.dto.AllFilesDto
import ru.kosti.googledrivemanager.dto.CreateItemDto
import ru.kosti.googledrivemanager.dto.UserDtoOnRequest
import ru.kosti.googledrivemanager.extention.toDto

@Service
class DriveService(
    private val drive: Drive,
    private val semaphore: Semaphore,
    private val userService: UserService,
    private val currentUser: UserDtoOnRequest
) {
    fun getAll(
        limit: Int,
        folderId: String = "1OGPa_sQSfshN8-NspxHJtagj47-0ZzEn",
        pageToken: String? = null
    ): AllFilesDto {
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
        CoroutineScope(Dispatchers.Default).launch {
            if (!checkCapabilities(newFile.parent))
                throw Exception()
            val file = File().apply {
                name = newFile.name
                parents = listOf(newFile.parent)
                mimeType = newFile.type.googleName
                kind = "drive#file"
            }
            val fileId = drive.files().create(file).execute()
            val parents = getFileParents(fileId.id)
            val usersEmails = parents.flatMap { parent ->
                userService.findAllByRootAvailablePath(parent).map { it.email }
            }
            usersEmails.forEach { email ->
                semaphore.withPermit {
                    val permission = Permission().apply {
                        type = "user"
                        emailAddress = email
                        role = "writer"
                    }
                    drive.permissions().create(fileId.id, permission).execute()
                }
            }
        }
    }

    private suspend fun checkCapabilities(path: String): Boolean {
        val user = userService.findByIdOrNull(currentUser.uuid)
        if (!user.isConformed)
            throw Exception()
        val availablePaths = getFileParents(path)
        availablePaths.forEach {
            if (it in (user.capabilities?.paths ?: emptySet()))
                return true
        }
        return false
    }

    private suspend fun getFileParents(fileId: String): List<String> {
        val parents = mutableListOf<String>()
        var currentFileId = fileId
        while (true) {
            val file = drive.files().get(currentFileId).setFields("parents").execute()
            val parentId = file.parents?.firstOrNull() ?: break
            parents.add(parentId)
            currentFileId = parentId
        }
        return parents
    }
}