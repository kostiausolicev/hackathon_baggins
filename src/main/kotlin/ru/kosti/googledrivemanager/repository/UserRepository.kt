package ru.kosti.googledrivemanager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kosti.googledrivemanager.entity.RoleEntity
import ru.kosti.googledrivemanager.entity.UserEntity
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findAllByRole(role: RoleEntity): List<UserEntity>
}