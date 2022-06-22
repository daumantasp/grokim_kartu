package com.dauma.grokimkartu.repositories.notifications.entities

class NotificationsPage(notifications: List<Notification>?, val isLast: Boolean) {
    val notifications: MutableList<Notification>?
    init {
        this.notifications = notifications?.toMutableList()
    }
}