package com.dauma.grokimkartu.repositories.notifications

import com.dauma.grokimkartu.repositories.notifications.entities.Notification
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationsPage
import com.dauma.grokimkartu.repositories.notifications.entities.UpdateNotification

interface NotificationsRepository {
    val pages: List<NotificationsPage>
    val unreadCount: Int?
    fun loadNextPage(onComplete: (NotificationsPage?, NotificationsErrors?) -> Unit)
    fun unreadCount(onComplete: (Int?, NotificationsErrors?) -> Unit)
    fun update(notificationId: Int, updateNotification: UpdateNotification, onComplete: (Notification?, NotificationsErrors?) -> Unit)
    fun reset()
    fun registerListener(id: String, listener: NotificationsListener)
    fun unregisterListener(id: String)
}