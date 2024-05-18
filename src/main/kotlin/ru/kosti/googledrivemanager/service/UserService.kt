package ru.kosti.googledrivemanager.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.dto.CreateUserDto
import ru.kosti.googledrivemanager.entity.UserEntity
import ru.kosti.googledrivemanager.repository.UserRepository
import java.util.*

@Service
class UserService(
    private val roleService: RoleService,
    private val userRepository: UserRepository,
    private val accessService: AccessService
) {
    suspend fun findByIdOrNull(userUuid: UUID) =
        userRepository.findByIdOrNull(userUuid)
            ?: throw Exception()

    fun findAllByRootAvailablePath(path: String): Set<UserEntity> {
        val roles = roleService.findByPathsContaining(path)
        val users = mutableListOf<UserEntity>()
        roles.forEach { role ->
            users.addAll(userRepository.findAllByRole(role))
        }
        return users.toSet()
    }

    fun updateRole(user: UUID, newRole: UUID) {
        val role = roleService.findByIdOrNull(newRole)
            ?: throw Exception()
        val old = userRepository.findByIdOrNull(user)
            ?: throw Exception()
        val new = UserEntity(
            uuid = old.uuid,
            firstName = old.firstName,
            lastName = old.lastName,
            email = old.email,
            role = role
        )
        userRepository.save(new)
    }

    suspend fun createUser(dto: CreateUserDto) {
        val role = roleService.findByIdOrNull(dto.role)
        val user = UserEntity(
            firstName = dto.firstName,
            lastName = dto.lastName,
            email = dto.email,
            role = role
        ).let { userRepository.save(it) }
        CoroutineScope(Dispatchers.Default).launch {
            user.role?.paths?.forEach { path ->
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