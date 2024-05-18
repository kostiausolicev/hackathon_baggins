package ru.kosti.googledrivemanager.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import ru.kosti.googledrivemanager.dto.user.UserDetailsImpl
import ru.kosti.googledrivemanager.repository.UserRepository
import java.util.*

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        val uuid = username?.let { UUID.fromString(it) } ?: throw Exception()
        val user = userRepository.findByIdOrNull(uuid)
            ?: throw Exception()
        return UserDetailsImpl(
            role = "ROLE_${user.role}",
            username = username,
            password = user.password
        )
    }
}