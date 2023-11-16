package com.dauma.grokimkartu.repositories.notifications.entities

import java.sql.Timestamp

data class Notification(
    val id: Int?,
    val user: NotificationUserConcise?,
    val name: String?,
    val description: String?,
    val createdAt: Timestamp?,
    val state: NotificationState?
)