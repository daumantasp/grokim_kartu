package com.dauma.grokimkartu.repositories.notifications.entities

import java.sql.Timestamp

data class Notification(
    var id: Int?,
    var user: NotificationUserConcise?,
    var isRead: Boolean?,
    var name: String?,
    var description: String?,
    var createdAt: Timestamp?
)