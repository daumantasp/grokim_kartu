package com.dauma.grokimkartu.repositories.notifications.paginator

import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.notifications.NotificationsErrors
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationsPage
import kotlinx.coroutines.flow.StateFlow

interface NotificationsPaginator {
    val pages: StateFlow<List<NotificationsPage>>
    suspend fun loadNextPage(): Result<NotificationsPage?, NotificationsErrors?>
    fun clear()
}