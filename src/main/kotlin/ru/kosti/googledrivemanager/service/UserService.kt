package ru.kosti.googledrivemanager.service

import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.entity.UserEntity

@Service
class UserService {
    fun findAllByRootAvailablePath(path: String): List<UserEntity> {
        return listOf(UserEntity(
            firstName = "",
            lastName = "",
            email = "kostiausolicev@gmail.com"
        ))
    }
}