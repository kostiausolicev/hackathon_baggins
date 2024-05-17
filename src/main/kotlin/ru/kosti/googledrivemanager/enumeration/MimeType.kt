package ru.kosti.googledrivemanager.enumeration

enum class MimeType(val googleName: String) {
    FOLDER("application/vnd.google-apps.folder"),
    DOC("application/vnd.google-apps.document"),
    FILE("application/vnd.google-apps.file"),
    SHEETS("application/vnd.google-apps.spreadsheet"),
    PRESENTATION("application/vnd.google-apps.presentation"),
    UNKNOWN("application/vnd.google-apps.unknown");

    companion object {
        fun findByGoogleName(name: String): MimeType =
            entries.find { it.googleName == name } ?: UNKNOWN
    }
}
