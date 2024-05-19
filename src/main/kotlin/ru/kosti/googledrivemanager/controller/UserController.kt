package ru.kosti.googledrivemanager.controller

import org.springframework.web.bind.annotation.*
import ru.kosti.googledrivemanager.aop.CheckToken
import ru.kosti.googledrivemanager.dto.user.*
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
    ): List<UserDto> =
        userService.findAll(limit, page)

    @GetMapping("/{uuid}")
    @CheckToken(Roles.ADMIN)
    suspend fun getByUuid(
        @RequestHeader("Authorization") token: String,
        @PathVariable uuid: UUID
    ): UserDto =
        userService.findDtoById(uuid)

    @PostMapping("/register")
    suspend fun register(@RequestBody dto: CreateUserDto) =
        userService.createUser(dto)

    @PostMapping("/auth")
    suspend fun auth(@RequestBody dto: AuthUserDto): SuccessAuthDto =
        userService.auth(dto)

    @PostMapping("/conform/{uuid}")
    @CheckToken(Roles.ADMIN)
    suspend fun conform(
        @RequestHeader("Authorization") token: String,
        @PathVariable uuid: UUID,
        @RequestParam role: UUID
    ) =
        userService.conform(userUuid = uuid, roleUuid = role)
    @PostMapping("/unconform/{uuid}")
    @CheckToken(Roles.ADMIN)
    suspend fun unconform(
        @RequestHeader("Authorization") token: String,
        @PathVariable uuid: UUID
    ) =
        userService.deleteUser(userUuid = uuid)

    @PatchMapping
    @CheckToken(Roles.ADMIN)
    suspend fun update(
        @RequestHeader("Authorization") token: String,
        @RequestBody dto: UpdateUserDto
    ) =
        userService.update(dto)
}