package ru.kosti.googledrivemanager.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.kosti.googledrivemanager.enumeration.Roles

class UserDetailsImpl(
    private val role: Roles,
    private val username: String,
    private val password: String
): UserDetails {
    override fun getAuthorities(): List<GrantedAuthority> =
        listOf(GrantedAuthority { role.name })

    override fun getPassword(): String =
        password

    override fun getUsername(): String =
        username

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}