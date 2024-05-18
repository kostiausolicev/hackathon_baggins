package ru.kosti.googledrivemanager.dto

import java.util.UUID

open class UserDtoOnRequest {
    lateinit var uuid: UUID
    lateinit var email: String
}