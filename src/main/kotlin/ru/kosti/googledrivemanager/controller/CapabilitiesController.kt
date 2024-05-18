package ru.kosti.googledrivemanager.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.kosti.googledrivemanager.dto.capabilities.CreateCapabilitiesDto
import ru.kosti.googledrivemanager.dto.capabilities.UpdateCapabilitiesDto
import ru.kosti.googledrivemanager.service.CapabilitiesService

@RestController
@RequestMapping("/capabilities")
class CapabilitiesController(
    private val capabilitiesService: CapabilitiesService
) {
    @GetMapping
    suspend fun getAll() =
        capabilitiesService.findAll()

    @PostMapping
    suspend fun create(@RequestBody dto: CreateCapabilitiesDto) {
        capabilitiesService.create(dto)
    }

    @PatchMapping
    suspend fun update(@RequestBody dto: UpdateCapabilitiesDto) {
        capabilitiesService.update(dto)
    }
}