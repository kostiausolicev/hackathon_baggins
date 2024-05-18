package ru.kosti.googledrivemanager.configuration

import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory.disable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import ru.kosti.googledrivemanager.configuration.filter.JwtFilter
import ru.kosti.googledrivemanager.enumeration.Roles


@Configuration
@EnableWebSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    private val userDetailsService: UserDetailsService,
    private val jwtFilter: JwtFilter
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers("/drive/**").authenticated()
            .requestMatchers("/user/conform/*").hasRole(Roles.ADMIN.name)
            .requestMatchers("/user/auth").permitAll()
            .requestMatchers("/user/register").permitAll()
            .requestMatchers(HttpMethod.PATCH, "/user").hasRole(Roles.ADMIN.name)
            .requestMatchers(HttpMethod.PATCH, "/capabilities").hasRole(Roles.ADMIN.name)
            .requestMatchers(HttpMethod.POST, "/capabilities").hasRole(Roles.ADMIN.name)
            .requestMatchers(HttpMethod.GET, "/capabilities").authenticated()
            .anyRequest().permitAll()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:8005")
        configuration.allowedMethods = listOf("GET", "POST", "PATCH")
        configuration.allowedHeaders = listOf("Authorization", "Content-Type")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder =
        BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager? {
        return config.authenticationManager
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider? {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }
}