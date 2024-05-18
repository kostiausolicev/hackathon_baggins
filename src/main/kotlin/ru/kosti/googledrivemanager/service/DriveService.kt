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
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.dto.AllFilesDto
import ru.kosti.googledrivemanager.dto.CreateItemDto
import ru.kosti.googledrivemanager.entity.UserEntity
import ru.kosti.googledrivemanager.extention.toDto

@Service
class DriveService(
    private val drive: Drive,
    private val semaphore: Semaphore,
    private val userService: UserService,
    private val jwtService: JwtService
) {
    suspend fun getAll(
        limit: Int,
        folderId: String = "1OGPa_sQSfshN8-NspxHJtagj47-0ZzEn",
        pageToken: String? = null,
        token: String
    ): AllFilesDto {
        val currentUserUuid = jwtService.decode(token).uuid
        val user = userService.findByIdOrNull(currentUserUuid)

        if (!checkCapabilities(folderId, user)) {
            throw Exception("User does not have the required capabilities")
        }
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

    suspend fun create(newFile: CreateItemDto, token: String) {
        val currentUserUuid = jwtService.decode(token).uuid
        val user = userService.findByIdOrNull(currentUserUuid)

        if (!checkCapabilities(newFile.parent, user)) {
            throw Exception("User does not have the required capabilities")
        }
        CoroutineScope(Dispatchers.Default).launch {

            val file = File().apply {
                name = newFile.name
                parents = listOf(newFile.parent)
                mimeType = newFile.type.googleName
                kind = "drive#file"
            }

            val fileId = withContext(Dispatchers.IO) {
                drive.files().create(file).execute().id
            }

            val parents = getFileParents(fileId)
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
                    withContext(Dispatchers.IO) {
                        drive.permissions().create(fileId, permission).execute()
                    }
                }
            }
        }
    }

    private suspend fun checkCapabilities(path: String, user: UserEntity): Boolean {
        val availablePaths = getFileParents(path)
        availablePaths.forEach {
            if (it in (user.capabilities?.paths ?: emptySet())) {
                return true
            }
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