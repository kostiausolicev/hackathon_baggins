package ru.kosti.googledrivemanager.configuration

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.auth.Credentials
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.IOException

@Configuration
class GoogleConfiguration(
    @Value("\${application.name}")
    private val applicationName: String,
    @Value("\${google.credentials_path}")
    private val credentialsFilePath: String
) {
    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

    @Throws(IOException::class)
    fun getCredentials(): Credentials {
        val key = ClassPathResource(credentialsFilePath).inputStream
        val scopes = DriveScopes.all()
        return GoogleCredentials.fromStream(key).createScoped(scopes)
    }

    @Bean
    fun service(): Drive {
        val credentials = getCredentials()
        val httpRequestInitializer = HttpCredentialsAdapter(credentials)

        return Drive.Builder(httpTransport, jsonFactory, httpRequestInitializer)
            .setApplicationName(applicationName)
            .build()
    }
}