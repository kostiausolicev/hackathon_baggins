package ru.kosti.googledrivemanager.entity

import jakarta.persistence.*
import ru.kosti.googledrivemanager.enumeration.Roles
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
    @Enumerated(EnumType.STRING)
    val role: Roles,
    val password: String,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "capabilities_uuid")
    val capabilities: CapabilitiesEntity? = null
) {
}