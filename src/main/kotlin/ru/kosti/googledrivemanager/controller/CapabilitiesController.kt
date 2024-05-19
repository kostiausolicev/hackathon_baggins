package ru.kosti.googledrivemanager.controller

import org.springframework.web.bind.annotation.*
import ru.kosti.googledrivemanager.aop.CheckToken
import ru.kosti.googledrivemanager.dto.capabilities.CreateCapabilitiesDto
import ru.kosti.googledrivemanager.dto.capabilities.UpdateCapabilitiesDto
import ru.kosti.googledrivemanager.enumeration.Roles
import ru.kosti.googledrivemanager.service.CapabilitiesService

@RestController
@RequestMapping("/capabilities")
class CapabilitiesController(
    private val capabilitiesService: CapabilitiesService
) {
    @GetMapping
    @CheckToken(Roles.USER)
    suspend fun getAll(
        @RequestHeader("Authorization") token: String,
    ) =
        capabilitiesService.findAll()

    @PostMapping
    @CheckToken(Roles.ADMIN)
    suspend fun create(
        @RequestHeader("Authorization") token: String,
        @RequestBody dto: CreateCapabilitiesDto
    ) =
        capabilitiesService.create(dto)

    @PatchMapping
    @CheckToken(Roles.ADMIN)
    suspend fun update(
        @RequestHeader("Authorization") token: String,
        @RequestBody dto: UpdateCapabilitiesDto
    ) =
        capabilitiesService.update(dto)
}