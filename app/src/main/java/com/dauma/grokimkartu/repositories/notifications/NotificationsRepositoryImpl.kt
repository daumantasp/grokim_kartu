package com.dauma.grokimkartu.repositories.notifications

import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.notifications.entities.Notification
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationsPage
import com.dauma.grokimkartu.repositories.notifications.paginator.NotificationsPaginator
import kotlinx.coroutines.flow.StateFlow

class NotificationsRepositoryImpl(
    override val paginator: NotificationsPaginator,
    private val user: User
) : NotificationsRepository {

    override val unreadCount: StateFlow<Int?> = paginator.unreadCount

    override suspend fun expand(notificationId: Int): Result<Notification?, NotificationsErrors?> =
        paginator.expand(notificationId = notificationId)

    override suspend fun reload(): Result<NotificationsPage?, NotificationsErrors?> {
        if (user.isUserLoggedIn()) {
            reset()
            return paginator.loadNextPage()
        } else {
            throw NotificationsException(NotificationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun reset() {
        if (user.isUserLoggedIn()) {
            paginator.clear()
        } else {
            throw NotificationsException(NotificationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun loginCompleted(isSuccessful: Boolean) {
        if (isSuccessful) {
            reset()
        }
    }
}