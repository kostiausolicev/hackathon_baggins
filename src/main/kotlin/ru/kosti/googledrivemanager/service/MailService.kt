package ru.kosti.googledrivemanager.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MailService(
    private val javaMailSender: JavaMailSender,
    @Value("\${spring.mail.sender.email}")
    private val senderEmail: String,
    @Value("\${spring.mail.sender.text}")
    private val senderText: String
) {
    fun sendVerificationMessage(email: String, uuid: UUID) {
        val message = SimpleMailMessage()
        message.from = senderEmail
        message.setTo(email)
        message.subject = "Подтвердите аккаунт"
        message.text = "Для подтверждения перейдите по ссылке:\n" +
                "http://localhost:8080/users/verify/$uuid"
        javaMailSender.send(message)
    }

    fun sendAcceptMessage(email: String) {
        val message = SimpleMailMessage()
        message.from = senderEmail
        message.setTo(email)
        message.subject = "Тестовое письмо"
        message.text = "Ваш аккаунт успешно подтвержден! Можете выполнять вход"
        javaMailSender.send(message)
    }

    fun sendDenyMessage(email: String) {
        val message = SimpleMailMessage()
        message.from = senderEmail
        message.setTo(email)
        message.subject = "Тестовое письмо"
        message.text = "Ваш аккаунт не подтвержден. \nМожете попробовать зарегистрирвоаться позднее или написать в поддержку"
        javaMailSender.send(message)
    }
}