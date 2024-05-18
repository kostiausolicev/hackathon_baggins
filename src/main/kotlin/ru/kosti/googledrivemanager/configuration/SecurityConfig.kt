package ru.kosti.googledrivemanager.configuration

import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory.disable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST
import ru.kosti.googledrivemanager.dto.UserDtoOnRequest


@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .authorizeHttpRequests { request ->
                request.anyRequest().permitAll()
            }
            .cors { disable() }
            .csrf().disable()
            .httpBasic { disable() }
            .formLogin { disable() }
        return http.build()
    }
}