package ru.kosti.googledrivemanager.controller

import org.springframework.web.bind.annotation.*
import ru.kosti.googledrivemanager.aop.CheckToken
import ru.kosti.googledrivemanager.dto.user.CreateUserDto
import ru.kosti.googledrivemanager.dto.user.UpdateUserDto
import ru.kosti.googledrivemanager.enumeration.Roles
import ru.kosti.googledrivemanager.service.UserService
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    @CheckToken(Roles.ADMIN)
    suspend fun getAll(
        @RequestHeader("Authorization") token: String,
        @RequestParam(required = false) limit: Int = 10,
        @RequestParam(required = false) page: Int = 0
    ) =
        userService.findAll(limit, page)

    @GetMapping("/{uuid}")
    @CheckToken(Roles.ADMIN)
    suspend fun getByUuid(
        @RequestHeader("Authorization") token: String,
        @PathVariable uuid: UUID
    ) =
        userService.findDtoById(uuid)

    @PostMapping("/register")
    suspend fun register(@RequestBody dto: CreateUserDto) =
        userService.createUser(dto)

    @PostMapping("/conform/{uuid}")
    @CheckToken(Roles.ADMIN)
    suspend fun conform(
        @RequestHeader("Authorization") token: String,
        @PathVariable uuid: UUID,
        @RequestParam role: UUID
    ) =
        userService.conform(userUuid = uuid, roleUuid = role)

    @PatchMapping
    @CheckToken(Roles.ADMIN)
    suspend fun update(
        @RequestHeader("Authorization") token: String,
        @RequestBody dto: UpdateUserDto
    ) =
        userService.update(dto)
}