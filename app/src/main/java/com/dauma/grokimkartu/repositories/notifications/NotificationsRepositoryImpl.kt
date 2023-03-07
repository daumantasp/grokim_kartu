package com.dauma.grokimkartu.repositories.notifications

import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.data.notifications.NotificationsDao
import com.dauma.grokimkartu.data.notifications.entities.NotificationResponse
import com.dauma.grokimkartu.data.notifications.entities.UpdateNotificationRequest
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.auth.LoginListener
import com.dauma.grokimkartu.repositories.notifications.entities.*
import com.dauma.grokimkartu.repositories.notifications.paginator.NotificationsPaginator
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationsRepositoryImpl(
    private val notificationsDao: NotificationsDao,
    private val _paginator: NotificationsPaginator,
    private val user: User
) : NotificationsRepository, LoginListener {
    override val paginator: NotificationsPaginator
        get() = _paginator

    private val _unreadCount: MutableStateFlow<Int?> = MutableStateFlow(null)
    override val unreadCount: StateFlow<Int?> = _unreadCount.asStateFlow()

    override suspend fun expand(notificationId: Int): Result<Notification?, NotificationsErrors?> {
        if (user.isUserLoggedIn()) {
            if (hasNotification(notificationId)) {
                val notification = getNotification(notificationId)!!
                readNotificationIfNeeded(notificationId)
                toggleNotificationActivity(notificationId)
                return Result(notification, null)
            } else {
                return Result(null, NotificationsErrors.UNKNOWN)
            }
        } else {
            throw NotificationsException(NotificationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private suspend fun readNotificationIfNeeded(notificationId: Int): Result<Notification?, NotificationsErrors?> {
        val previousNotification = getNotification(notificationId)
        if (previousNotification?.state == NotificationState.UNREAD) {
            val updateNotification = UpdateNotification(isRead = true)
            val updateResult = update(notificationId, updateNotification)
            updateResult.data?.let {
                if (it.state != NotificationState.UNREAD) {
                    previousNotification.state = it.state
                    _unreadCount.value = _unreadCount.value?.let { it - 1 }
                }
            }
            return updateResult
        } else {
            return Result(null, NotificationsErrors.UNKNOWN)
        }
    }

    private suspend fun update(
        notificationId: Int,
        updateNotification: UpdateNotification,
    ): Result<Notification?, NotificationsErrors?> {
        if (user.isUserLoggedIn()) {
            val updateNotificationsRequest = UpdateNotificationRequest(
                isRead = updateNotification.isRead
            )
            val response = notificationsDao.update(notificationId, updateNotificationsRequest, user.getBearerAccessToken()!!)
            val status = response.status
            val notificationResponse = response.data
            if (status.isSuccessful && notificationResponse != null) {
                val notification = toNotification(notificationResponse)
                return Result(notification, null)
            } else {
                return Result(null, NotificationsErrors.UNKNOWN)
            }
        } else {
            throw NotificationsException(NotificationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun reload(): Result<NotificationsPage?, NotificationsErrors?> {
        if (user.isUserLoggedIn()) {
            reset()
            return _paginator.loadNextPage()
        } else {
            throw NotificationsException(NotificationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun reset() {
        if (user.isUserLoggedIn()) {
            _paginator.clear()
        } else {
            throw NotificationsException(NotificationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun hasNotification(notificationId: Int) : Boolean {
        return getNotification(notificationId) != null
    }

    private fun getNotification(notificationId: Int) : Notification? {
        for (page in _paginator.pages.value) {
            page.notifications?.let { notifications ->
                for (notification in notifications) {
                    if (notification.id == notificationId) {
                        return notification
                    }
                }
            }
        }
        return null
    }

    private fun toggleNotificationActivity(notificationId: Int) {
        for (page in _paginator.pages.value) {
            page.notifications?.let { notifications ->
                for (notification in notifications) {
                    if (notification.id == notificationId) {
                        if (notification.state == NotificationState.READ || notification.state == NotificationState.UNREAD) {
                            notification.state = NotificationState.EXPANDED
                        } else {
                            notification.state = NotificationState.READ
                        }
                    } else if (notification.state == NotificationState.EXPANDED) {
                        notification.state = NotificationState.READ
                    }
                }
            }
        }
    }

    private fun toNotification(notificationResponse: NotificationResponse) : Notification {
        val state = if (notificationResponse.isRead == true) NotificationState.READ else NotificationState.UNREAD
        return Notification(
            id = notificationResponse.id,
            user = NotificationUserConcise(
                id = notificationResponse.user?.id,
                name = notificationResponse.user?.name
            ),
            name = notificationResponse.name,
            description = notificationResponse.description,
            createdAt = notificationResponse.createdAt,
            state = state
        )
    }

    override fun loginCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        if (isSuccessful) {
            reset()
        }
    }
}