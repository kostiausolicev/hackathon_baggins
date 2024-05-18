package ru.kosti.googledrivemanager.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.entity.RoleEntity
import ru.kosti.googledrivemanager.repository.RoleRepository
import java.util.*

@Service
class RoleService(
    private val roleRepository: RoleRepository
) {
    fun findByPathsContaining(path: String): List<RoleEntity> =
        roleRepository.findByPathsContaining(path)

    fun findByIdOrNull(newRole: UUID): RoleEntity? =
        roleRepository.findByIdOrNull(newRole)
}