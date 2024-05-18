package ru.kosti.googledrivemanager.controller

import org.springframework.web.bind.annotation.*
import ru.kosti.googledrivemanager.dto.CreateUserDto
import ru.kosti.googledrivemanager.service.UserService
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping
    suspend fun create(@RequestBody dto: CreateUserDto) =
        userService.createUser(dto)

    @PostMapping("/conform/{uuid}")
    suspend fun conform(@PathVariable uuid: UUID, @RequestParam role: UUID) =
        userService.conform(userUuid = uuid, roleUuid = role)
}