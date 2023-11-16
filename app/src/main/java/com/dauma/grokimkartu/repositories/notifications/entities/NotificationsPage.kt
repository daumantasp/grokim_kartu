package com.dauma.grokimkartu.repositories.notifications.entities

data class NotificationsPage(
    val notifications: List<Notification>,
    val isLast: Boolean
)