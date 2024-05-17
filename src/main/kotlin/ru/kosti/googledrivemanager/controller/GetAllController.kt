package ru.kosti.googledrivemanager.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.kosti.googledrivemanager.service.DriveService

@RestController
@RequestMapping("/drive")
class GetAllController(
    private val driveService: DriveService
) {
    @GetMapping
    fun getAll(
        @RequestParam(required = false) limit: Int = 10,
        @RequestParam(required = false) pageToken: String? = null
    ) =
        driveService.getAll(limit = limit, pageToken = pageToken)
}