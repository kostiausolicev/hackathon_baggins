package ru.kosti.googledrivemanager.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.dto.CreateUserDto
import ru.kosti.googledrivemanager.entity.UserEntity
import ru.kosti.googledrivemanager.enumeration.Roles
import ru.kosti.googledrivemanager.repository.UserRepository
import java.util.*

@Service
class UserService(
    private val capabilitiesService: CapabilitiesService,
    private val userRepository: UserRepository,
    private val accessService: AccessService
) {
    suspend fun findByIdOrNull(userUuid: UUID) =
        userRepository.findByIdOrNull(userUuid)
            ?: throw Exception()

    suspend fun findAllByRootAvailablePath(path: String): Set<UserEntity> {
        val roles = capabilitiesService.findByPathsContaining(path)
        val users = mutableListOf<UserEntity>()
        roles.forEach { role ->
            users.addAll(userRepository.findAllByRole(role))
        }
        return users.toSet()
    }

    suspend fun update(user: UUID, newRole: UUID) {
        val capabilities = capabilitiesService.findByIdOrNull(newRole)
            ?: throw Exception()
        val old = userRepository.findByIdOrNull(user)
            ?: throw Exception()
        val new = UserEntity(
            uuid = old.uuid,
            firstName = old.firstName,
            lastName = old.lastName,
            email = old.email,
            role = old.role,
            capabilities = capabilities
        )
        userRepository.save(new)
    }

    suspend fun createUser(dto: CreateUserDto) {
        if (!dto.email.contains("@"))
            throw Exception()
        if (dto.email.split('@')[1] != "gmail.com")
            throw Exception()
        UserEntity(
            firstName = dto.firstName,
            lastName = dto.lastName,
            role = Roles.USER,
            email = dto.email
        ).let { userRepository.save(it) }
    }

    suspend fun conform(userUuid: UUID, roleUuid: UUID) {
        val capabilities = capabilitiesService.findByIdOrNull(roleUuid)
            ?: throw Exception()
        val ent = userRepository.findByIdOrNull(userUuid)
            ?: throw Exception()
        val user = UserEntity(
            uuid = ent.uuid,
            firstName = ent.firstName,
            lastName = ent.lastName,
            email = ent.email,
            isConformed = true,
            role = Roles.USER,
            capabilities = capabilities
        ).let { userRepository.save(it) }
        CoroutineScope(Dispatchers.Default).launch {
            user.capabilities?.paths?.forEach { path ->
                accessService.addAccess(user.email, path)
            }
        }
    }

    suspend fun deleteUser(userUuid: UUID) {
        val user = findByIdOrNull(userUuid)
        userRepository.delete(user)
        CoroutineScope(Dispatchers.Default).launch {
            accessService.removeAccess(user.email)
        }
    }
}