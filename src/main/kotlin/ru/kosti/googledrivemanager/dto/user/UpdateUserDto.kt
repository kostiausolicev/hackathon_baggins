package ru.kosti.googledrivemanager.dto.user

import com.fasterxml.jackson.annotation.JsonProperty
import ru.kosti.googledrivemanager.enumeration.Roles
import java.util.UUID

data class UpdateUserDto(
    val uuid: UUID,
    val email: String,
//    @JsonProperty(required = false)
    val firstName: String? = null,
//    @JsonProperty(required = false)
    val lastName: String? = null,
//    @JsonProperty(required = false)
    val password: String? = null,
//    @JsonProperty(required = false)
    val capabilities: UUID? = null,
//    @JsonProperty(required = false)
    val role: Roles? = null,
//    @JsonProperty(required = false)
    val repeatPassword: String? = null
)
