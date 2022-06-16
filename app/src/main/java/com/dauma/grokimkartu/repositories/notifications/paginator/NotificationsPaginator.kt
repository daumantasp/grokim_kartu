package com.dauma.grokimkartu.repositories.notifications.paginator

import com.dauma.grokimkartu.data.notifications.entities.NotificationsResponse
import com.dauma.grokimkartu.repositories.notifications.NotificationsErrors

interface NotificationsPaginator {
    val pages: List<NotificationsResponse>
    val pageSize: Int
    fun loadNextPage(accessToken: String, onComplete: (NotificationsResponse?, NotificationsErrors?) -> Unit)
    fun clear()
}