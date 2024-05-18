package ru.kosti.googledrivemanager.controller

import org.springframework.web.bind.annotation.*
import ru.kosti.googledrivemanager.dto.CreateItemDto
import ru.kosti.googledrivemanager.service.AccessService
import ru.kosti.googledrivemanager.service.DriveService

@RestController
@RequestMapping("/drive")
class DriverController(
    private val driveService: DriveService
) {
    @GetMapping("/{root}")
    fun getAllByRoot(
        @RequestParam(required = false) limit: Int = 10,
        @RequestParam(required = false) pageToken: String? = null,
        @PathVariable root: String
    ) =
        driveService.getAll(limit = limit, pageToken = pageToken, folderId = root)

    @GetMapping
    fun getAll(
        @RequestParam(required = false) limit: Int = 10,
        @RequestParam(required = false) pageToken: String? = null
    ) =
        driveService.getAll(limit = limit, pageToken = pageToken)

    @PostMapping
    suspend fun create(@RequestBody createItemDto: CreateItemDto) =
        driveService.create(createItemDto)
}