package ru.kosti.googledrivemanager.service

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.Permission
import com.google.api.services.drive.model.PermissionList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.enumeration.MimeType

@Service
class AccessService(
    private val drive: Drive,
    private val semaphore: Semaphore
) {
    suspend fun removeAccess(userEmail: String) {
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
    }

    suspend fun addAccess(userEmail: String, rootFolderId: String) {
        CoroutineScope(Dispatchers.Default).launch {
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
    }

    private fun addEditPermission(userEmail: String, fileId: String) {
        val permission = Permission().apply {
            type = "user"
            role = "writer"
            emailAddress = userEmail
        }
        drive.permissions().create(fileId, permission).execute()
    }
}