package ru.kosti.googledrivemanager.service

import com.google.api.services.drive.Drive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatusCode
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.dto.user.*
import ru.kosti.googledrivemanager.entity.UserEntity
import ru.kosti.googledrivemanager.enumeration.Roles
import ru.kosti.googledrivemanager.exception.ApiException
import ru.kosti.googledrivemanager.extention.toDto
import ru.kosti.googledrivemanager.repository.UserRepository
import java.util.*

@Service
class UserService(
    private val capabilitiesService: CapabilitiesService,
    private val userRepository: UserRepository,
    private val accessService: AccessService,
    private val jwtService: JwtService,
    private val drive: Drive,
    private val mail: MailService,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    suspend fun findDtoById(userUuid: UUID) =
        userRepository.findByIdOrNull(userUuid)
            ?.toDto(drive)
            ?: throw ApiException(HttpStatusCode.valueOf(404), "User not found")

    suspend fun findById(userUuid: UUID) =
        userRepository.findByIdOrNull(userUuid)
            ?: throw ApiException(HttpStatusCode.valueOf(404), "User not found")

    suspend fun findAllByRootAvailablePath(path: String): Set<UserEntity> {
        val roles = capabilitiesService.findByPathsContaining(path)
        val users = mutableListOf<UserEntity>()
        roles.forEach { role ->
            users.addAll(userRepository.findAllByCapabilities(role))
        }
        return users.toSet()
    }

    suspend fun findAll(limit: Int, page: Int = 0): List<UserDto> {
        val pageable = PageRequest.of(page, limit, Sort.by(Sort.Order.asc("isConformed")))
        val users = userRepository.findAll(pageable)
        return users.content.mapNotNull {
            if (it.emailConform)
                it.toDto(drive)
            else null
        }
    }

    suspend fun update(dto: UpdateUserDto) {
        val old = userRepository.findByIdOrNull(dto.uuid)
            ?: throw ApiException(HttpStatusCode.valueOf(404), "User not found")
        val capabilities = if (dto.capabilities != null)
            capabilitiesService.findByIdOrNull(dto.capabilities)
                ?: throw ApiException(HttpStatusCode.valueOf(404), "Capabilities not found")
        else old.capabilities
        val new = UserEntity(
            uuid = old.uuid,
            firstName = dto.firstName ?: old.firstName,
            lastName = dto.lastName ?: old.lastName,
            email = old.email,
            isConformed = old.isConformed,
            emailConform = old.emailConform,
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

    suspend fun createUser(dto: CreateUserDto) {
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
        mail.sendVerificationMessage(user.email, user.uuid)
//        return jwtService.generate(email = user.email, uuid = user.uuid)
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
            emailConform = ent.emailConform,
            isConformed = true,
            role = Roles.USER,
            password = ent.password,
            capabilities = capabilities
        ).let { userRepository.save(it) }
        mail.sendAcceptMessage(user.email)
        user.capabilities?.paths?.forEach { path ->
            accessService.addAccess(user.email, path)
        }
    }

    suspend fun deleteUser(userUuid: UUID) {
        val user = userRepository.findByIdOrNull(userUuid)
            ?: throw ApiException(HttpStatusCode.valueOf(404), "User not found")
        userRepository.delete(user)
        mail.sendDenyMessage(user.email)
        CoroutineScope(Dispatchers.Default).launch {
            accessService.removeAccess(user.email)
        }
    }

    suspend fun verifyEmail(uuid: UUID): String {
        val ent = userRepository.findByIdOrNull(uuid)
            ?: throw Exception()
        UserEntity(
            uuid = ent.uuid,
            firstName = ent.firstName,
            lastName = ent.lastName,
            email = ent.email,
            emailConform = true,
            role = Roles.USER,
            password = ent.password,
        ).let { userRepository.save(it) }
        return "ok"
    }

    fun auth(dto: AuthUserDto): SuccessAuthDto {
        val user = userRepository.findByEmail(dto.email)
            ?: throw ApiException(HttpStatusCode.valueOf(404), "User not found")
        if (!passwordEncoder.matches(dto.password, user.password))
            throw ApiException(HttpStatusCode.valueOf(405), "Wrong password")
        return SuccessAuthDto(
            token = jwtService.generate(email = user.email, uuid = user.uuid),
            role = user.role
        )
    }
}