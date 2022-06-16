package com.dauma.grokimkartu.data.notifications

class NotificationsDaoResponseStatus (
    val isSuccessful: Boolean,
    val error: Errors?
) {
    enum class Errors {
        NOTIFICATION_NOT_FOUND,
        FORBIDDEN,
        UNKNOWN
    }
}