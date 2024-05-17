package ru.kosti.googledrivemanager.configuration

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["ru.kosti.googledrivemanager.repository"])
@EntityScan(basePackages = ["ru.kosti.googledrivemanager.entity"])
@ComponentScan(basePackages = ["ru.kosti.googledrivemanager"])
class ApplicationConfig {
}