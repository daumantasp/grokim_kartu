package com.dauma.grokimkartu.repositories.notifications

class NotificationsException (error: NotificationsErrors)
    : Exception(error.toString()) {}

enum class NotificationsErrors {
    USER_NOT_LOGGED_IN,
    UNKNOWN
}