package ru.kosti.googledrivemanager.service

import com.google.api.services.drive.Drive
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.dto.UpdateRoleDto
import ru.kosti.googledrivemanager.entity.CapabilitiesEntity
import ru.kosti.googledrivemanager.extention.toDto
import ru.kosti.googledrivemanager.repository.RoleRepository
import java.util.*

@Service
class CapabilitiesService(
    private val drive: Drive,
    private val roleRepository: RoleRepository
) {
    fun findByPathsContaining(path: String): List<CapabilitiesEntity> =
        roleRepository.findByPathsContaining(path)

    fun findByIdOrNull(newRole: UUID): CapabilitiesEntity? =
        roleRepository.findByIdOrNull(newRole)

    fun update(dto: UpdateRoleDto) {
        roleRepository.findByIdOrNull(dto.uuid)
            ?: throw Exception()
        CapabilitiesEntity(
            uuid = dto.uuid,
            title = dto.title,
            paths = dto.paths.map { it.id }.toSet()
        ).also { roleRepository.save(it) }
    }

    fun findAll() =
        roleRepository.findAll()
            .map { it.toDto(drive = drive) }
}