package com.dauma.grokimkartu.repositories.notifications

import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.notifications.entities.Notification
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationsPage
import com.dauma.grokimkartu.repositories.notifications.paginator.NotificationsPaginator
import kotlinx.coroutines.flow.StateFlow

interface NotificationsRepository {
    val paginator: NotificationsPaginator
    val unreadCount: StateFlow<Int?>
    suspend fun expand(notificationId: Int): Result<Notification?, NotificationsErrors?>
    suspend fun reload(): Result<NotificationsPage?, NotificationsErrors?>
}