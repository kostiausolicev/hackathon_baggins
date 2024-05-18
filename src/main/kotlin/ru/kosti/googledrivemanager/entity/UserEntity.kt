package ru.kosti.googledrivemanager.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val uuid: UUID = UUID.randomUUID(),
    @Column(name = "first_name")
    val firstName: String,
    @Column(name = "last_name")
    val lastName: String,
    val email: String,
    val isConformed: Boolean = false,
    @ManyToOne(fetch = FetchType.EAGER)
    val role: RoleEntity? = null
)