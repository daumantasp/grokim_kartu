package com.dauma.grokimkartu.repositories.notifications

import com.dauma.grokimkartu.repositories.notifications.entities.Notification
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationsPage

interface NotificationsRepository {
    val pages: List<NotificationsPage>
    val unreadCount: Int?
    fun loadNextPage(onComplete: (NotificationsPage?, NotificationsErrors?) -> Unit)
    fun unreadCount(onComplete: (Int?, NotificationsErrors?) -> Unit)
    fun activate(notificationId: Int, onComplete: (Notification?, NotificationsErrors?) -> Unit)
    fun reload(onComplete: (NotificationsPage?, NotificationsErrors?) -> Unit)
    fun registerListener(id: String, listener: NotificationsListener)
    fun unregisterListener(id: String)
}