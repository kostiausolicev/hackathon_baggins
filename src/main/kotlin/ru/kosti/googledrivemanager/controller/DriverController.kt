package ru.kosti.googledrivemanager.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.kosti.googledrivemanager.dto.CreateItemDto
import ru.kosti.googledrivemanager.service.DriveService

@RestController
@RequestMapping("/drive")
class DriverController(
    private val driveService: DriveService
) {
    @GetMapping
    fun getAll(
        @RequestParam(required = false) limit: Int = 10,
        @RequestParam(required = false) pageToken: String? = null
    ) =
        driveService.getAll(limit = limit, pageToken = pageToken)

    @PostMapping
    fun create(@RequestBody createItemDto: CreateItemDto) =
        driveService.create(createItemDto)
}