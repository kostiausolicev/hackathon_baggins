package ru.kosti.googledrivemanager.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.dto.user.CreateUserDto
import ru.kosti.googledrivemanager.dto.user.UpdateUserDto
import ru.kosti.googledrivemanager.dto.user.UserDetailsImpl
import ru.kosti.googledrivemanager.entity.UserEntity
import ru.kosti.googledrivemanager.enumeration.Roles
import ru.kosti.googledrivemanager.repository.UserRepository
import java.util.*

@Service
class UserService(
    private val capabilitiesService: CapabilitiesService,
    private val userRepository: UserRepository,
    private val accessService: AccessService,
    private val jwtService: JwtService,
    private val passwordEncoder: BCryptPasswordEncoder
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

    suspend fun update(dto: UpdateUserDto) {
        val old = userRepository.findByIdOrNull(dto.uuid)
            ?: throw Exception()
        val capabilities = if (dto.capabilities != null)
            capabilitiesService.findByIdOrNull(dto.capabilities)
                ?: throw Exception()
        else old.capabilities
        val new = UserEntity(
            uuid = old.uuid,
            firstName = dto.firstName ?: old.firstName,
            lastName = dto.lastName ?: old.lastName,
            email = old.email,
            role = dto.role ?: old.role,
            password = old.password,
            capabilities = capabilities
        ).let { userRepository.save(it) }
        CoroutineScope(Dispatchers.Default).launch {
            accessService.removeAccess(new.email)
            new.capabilities?.paths?.forEach { path ->
                accessService.addAccess(new.email, path)
            }
        }
    }

    suspend fun createUser(dto: CreateUserDto): String {
        if (!dto.email.contains("@"))
            throw Exception()
        if (dto.email.split('@')[1] != "gmail.com")
            throw Exception()
        if (dto.password != dto.repeatPassword)
            throw Exception()
        val user = UserEntity(
            firstName = dto.firstName,
            lastName = dto.lastName,
            role = Roles.USER,
            password = passwordEncoder.encode(dto.password),
            email = dto.email
        ).let { userRepository.save(it) }
        return jwtService.generate(email = user.email, uuid = user.uuid)
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
            password = ent.password,
            capabilities = capabilities
        ).let { userRepository.save(it) }
        user.capabilities?.paths?.forEach { path ->
            accessService.addAccess(user.email, path)
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