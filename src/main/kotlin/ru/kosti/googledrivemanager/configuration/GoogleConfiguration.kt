package ru.kosti.googledrivemanager.configuration

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
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
    @Value("\${google.credentials_path}")
    private val credentialsFilePath: String,
    @Value("\${google.credentials}")
    private val credentialsFile: String
) {
    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

    @Throws(IOException::class)
    fun getCredentials(): Credentials {
        val key = credentialsFile.byteInputStream()
        val scopes = DriveScopes.all()
        return GoogleCredentials.fromStream(key).createScoped(scopes)
    }

    @Bean
    fun service(): Drive {
        val credentials = getCredentials()
        val httpRequestInitializer = HttpCredentialsAdapter(credentials)

        return Drive.Builder(httpTransport, jsonFactory, httpRequestInitializer)
            .setApplicationName("Google Drive Manager")
            .build()
    }
}