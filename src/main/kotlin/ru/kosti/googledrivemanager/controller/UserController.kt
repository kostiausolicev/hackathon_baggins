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
    @PostMapping("/register")
    suspend fun register(@RequestBody dto: CreateUserDto) =
        userService.createUser(dto)

    @PostMapping("/conform/{uuid}")
    @CheckToken(Roles.ADMIN)
    suspend fun conform(@PathVariable uuid: UUID, @RequestParam role: UUID) =
        userService.conform(userUuid = uuid, roleUuid = role)

    @PatchMapping
    suspend fun update(@RequestBody dto: UpdateUserDto) =
        userService.update(dto)
}