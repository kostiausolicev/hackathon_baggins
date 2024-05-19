package ru.kosti.googledrivemanager.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
@EnableReactiveMethodSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .csrf { disable() }
            .cors { disable() }
            .authorizeHttpRequests()
//            .requestMatchers("/drive/**").authenticated()
//            .requestMatchers("/user/conform/*").hasRole(Roles.ADMIN.name)
//            .requestMatchers("/user/auth").permitAll()
//            .requestMatchers("/user/register").permitAll()
//            .requestMatchers(HttpMethod.PATCH, "/user").hasRole(Roles.ADMIN.name)
//            .requestMatchers(HttpMethod.PATCH, "/capabilities").hasRole(Roles.ADMIN.name)
//            .requestMatchers(HttpMethod.POST, "/capabilities").hasRole(Roles.ADMIN.name)
//            .requestMatchers(HttpMethod.GET, "/capabilities").authenticated()
            .anyRequest().permitAll()
        return http.build()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder =
        BCryptPasswordEncoder()
}