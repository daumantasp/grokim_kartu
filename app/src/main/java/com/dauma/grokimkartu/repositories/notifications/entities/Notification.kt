package com.dauma.grokimkartu.repositories.notifications.entities

import java.sql.Timestamp

class Notification {
    var id: Int?
    var user: NotificationUserConcise?
    var name: String?
    var description: String?
    var createdAt: Timestamp?
    var state: NotificationState?

    constructor(
        id: Int?,
        user: NotificationUserConcise?,
        name: String?,
        description: String?,
        createdAt: Timestamp?,
        state: NotificationState?
    ) {
        this.id = id
        this.user = user
        this.name = name
        this.description = description
        this.createdAt = createdAt
        this.state = state
    }

    constructor(notification: Notification) {
        this.id = notification.id
        this.user = NotificationUserConcise(
            id = notification.user?.id,
            name = notification.user?.name
        )
        this.name = notification.name
        this.description = notification.description
        this.createdAt = notification.createdAt
        this.state = notification.state
    }
}