package com.dauma.grokimkartu.repositories.notifications.entities

data class NotificationsPage (
    val players: List<Notification>?,
    val isLast: Boolean
)