package ru.kosti.googledrivemanager.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.dto.UserDtoOnRequest
import java.util.*

@Service
class JwtService(
    @Value("\${jwt.secret}")
    val secret: String,
) {
    fun decode(token: String): UserDtoOnRequest {
        val data = JWT.require(Algorithm.HMAC256(secret))
            .build()
            .verify(token.split(' ')[1])
        val email = data.getClaim("email").asString()
        val uuid = data.getClaim("uuid").asString()
            .let { UUID.fromString(it) }
        return UserDtoOnRequest(
            email = email,
            uuid = uuid
        )
    }

    fun encode(email: String, uuid: UUID): String {
        return JWT.create()
            .withClaim("uuid", uuid.toString())
            .withClaim("email", email)
            .sign(Algorithm.HMAC256(secret))
    }
}