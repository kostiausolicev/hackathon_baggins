package ru.kosti.googledrivemanager.dto.user

import java.util.UUID

data class UserDtoOnRequest(
    var uuid: UUID,
    var email: String
)