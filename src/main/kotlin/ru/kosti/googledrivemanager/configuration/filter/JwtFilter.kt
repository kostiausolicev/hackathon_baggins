package ru.kosti.googledrivemanager.configuration.filter

import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.kosti.googledrivemanager.service.JwtService
import ru.kosti.googledrivemanager.service.UserDetailsServiceImpl

@Component
class JwtFilter(
    private val tokenService: JwtService,
    private val userService: UserDetailsServiceImpl
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        var token: String? = null
        var u: UserDetails? = null
        try {
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7)
                u = tokenService.decode(token)
                    .let { userService.loadUserByUsername(it.uuid.toString()) }
            }
            if (u != null && SecurityContextHolder.getContext().authentication == null) {
                val upatoken = UsernamePasswordAuthenticationToken(
                    u.username,
                   null,
                    u.authorities
                )
                upatoken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = upatoken
            }
            filterChain.doFilter(request, response)
        } catch (e: TokenExpiredException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
        } catch (e: SignatureVerificationException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
        } catch (e: Exception) {
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY)
        }
    }
}