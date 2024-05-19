package ru.kosti.googledrivemanager.enumeration

enum class Roles(
    val order: Int
) {
    USER(Int.MAX_VALUE),
    ADMIN(Int.MIN_VALUE)
}
