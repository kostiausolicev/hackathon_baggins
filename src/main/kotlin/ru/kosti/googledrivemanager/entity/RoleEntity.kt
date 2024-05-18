package ru.kosti.googledrivemanager.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "roles")
class RoleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val uuid: UUID,
    val title: String,
    @ElementCollection
    @CollectionTable(name = "role_paths", joinColumns = [JoinColumn(name = "role_uuid")])
    @Column(name = "path")
    val paths: Set<String>
)