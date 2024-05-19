package ru.kosti.googledrivemanager.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "capabilities")
class CapabilitiesEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val uuid: UUID = UUID.randomUUID(),
    val title: String,
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "capabilities_paths",
        joinColumns = [JoinColumn(name = "capabilities_uuid")]
    )
    @Column(name = "path")
    val paths: Set<String>
)
