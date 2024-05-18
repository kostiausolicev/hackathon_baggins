package ru.kosti.googledrivemanager.service

import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.entity.UserEntity
import ru.kosti.googledrivemanager.repository.RoleRepository
import ru.kosti.googledrivemanager.repository.UserRepository

@Service
class UserService(
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository
) {
    fun findAllByRootAvailablePath(path: String): Set<UserEntity> {
        val roles = roleRepository.findByPathsContaining(path)
        val users = mutableListOf<UserEntity>()
        roles.forEach { role ->
            users.addAll(userRepository.findAllByRole(role))
        }
        return users.toSet()
    }
}