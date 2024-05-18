package ru.kosti.googledrivemanager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kosti.googledrivemanager.entity.CapabilitiesEntity
import java.util.*

@Repository
interface RoleRepository : JpaRepository<CapabilitiesEntity, UUID> {
    fun findByPathsContaining(searchString: String): List<CapabilitiesEntity>
}

