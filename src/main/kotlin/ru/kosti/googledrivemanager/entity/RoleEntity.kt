package ru.kosti.googledrivemanager.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(name = "roles")
class RoleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val uuid: UUID,
    val title: String,
    val order: Int,
    @JdbcTypeCode(SqlTypes.JSON)
    val paths: Set<String>
)