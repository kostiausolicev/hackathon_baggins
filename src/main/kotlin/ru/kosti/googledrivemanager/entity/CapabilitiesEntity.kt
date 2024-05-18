package ru.kosti.googledrivemanager.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "capabilities")
class CapabilitiesEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val uuid: UUID,
    val title: String,
    @ElementCollection
    @CollectionTable(name = "capabilities_paths", joinColumns = [JoinColumn(name = "capabilities_uuid")])
    @Column(name = "capabilities")
    val paths: Set<String>
)