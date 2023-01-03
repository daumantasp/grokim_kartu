package com.dauma.grokimkartu.repositories.notifications

import com.dauma.grokimkartu.data.notifications.NotificationsDao
import com.dauma.grokimkartu.data.notifications.entities.NotificationResponse
import com.dauma.grokimkartu.data.notifications.entities.NotificationsResponse
import com.dauma.grokimkartu.data.notifications.entities.UpdateNotificationRequest
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.auth.LoginListener
import com.dauma.grokimkartu.repositories.notifications.entities.*
import com.dauma.grokimkartu.repositories.notifications.paginator.NotificationsPaginator
import com.dauma.grokimkartu.repositories.profile.ProfileListener
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors

class NotificationsRepositoryImpl(
    private val notificationsDao: NotificationsDao,
    private val paginator: NotificationsPaginator,
    private val user: User
) : NotificationsRepository, LoginListener, ProfileListener {
    private val _pages: MutableList<NotificationsPage> = mutableListOf()
    private var _unreadCount: Int? = null
    private val notificationsListeners: MutableMap<String, NotificationsListener> = mutableMapOf()

    override val pages: List<NotificationsPage>
        get() = _pages

    override fun loadNextPage(onComplete: (NotificationsPage?, NotificationsErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            paginator.loadNextPage(user.getBearerAccessToken()!!) { notificationsResponse, isLastPage ->
                if (notificationsResponse != null) {
                    val notificationsPage = toNotificationsPage(notificationsResponse)
                    _pages.add(notificationsPage)
                    onComplete(notificationsPage, null)
                } else {
                    onComplete(null, NotificationsErrors.UNKNOWN)
                }
            }
        } else {
            throw NotificationsException(NotificationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun activate(
        notificationId: Int,
        onComplete: (Notification?, NotificationsErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            if (hasNotification(notificationId)) {
                val notification = getNotification(notificationId)!!
                readNotificationIfNeeded(notificationId) { _, _ -> }
                toggleNotificationActivity(notificationId)
                onComplete(notification, null)
            } else {
                onComplete(null, NotificationsErrors.UNKNOWN)
            }
        } else {
            throw NotificationsException(NotificationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun readNotificationIfNeeded(
        notificationId: Int,
        onComplete: (Notification?, NotificationsErrors?) -> Unit
    ) {
        val previousNotification = getNotification(notificationId)
        if (previousNotification?.state == NotificationState.UNREAD) {
            val updateNotification = UpdateNotification(isRead = true)
            update(notificationId, updateNotification) { updatedNotification, notificationsErrors ->
                updatedNotification?.let {
                    if (it.state != NotificationState.UNREAD) {
                        previousNotification.state = it.state
                        _unreadCount = _unreadCount?.let { it - 1 }
                    }
                }
                onComplete(updatedNotification, notificationsErrors)
            }
        } else {
            onComplete(null, NotificationsErrors.UNKNOWN)
        }
    }

    private fun update(
        notificationId: Int,
        updateNotification: UpdateNotification,
        onComplete: (Notification?, NotificationsErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            val updateNotificationsRequest = UpdateNotificationRequest(
                isRead = updateNotification.isRead
            )
            notificationsDao.update(notificationId, updateNotificationsRequest, user.getBearerAccessToken()!!) { notificationResponse, notificationsDaoResponseStatus ->
                if (notificationsDaoResponseStatus.isSuccessful && notificationResponse != null) {
                    val notification = toNotification(notificationResponse)
                    onComplete(notification, null)
                } else {
                    onComplete(null, NotificationsErrors.UNKNOWN)
                }
            }
        } else {
            throw NotificationsException(NotificationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun reload(onComplete: (NotificationsPage?, NotificationsErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            reset()
            loadNextPage(onComplete)
        } else {
            throw NotificationsException(NotificationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun reset() {
        if (user.isUserLoggedIn()) {
            _pages.clear()
            paginator.clear()
        } else {
            throw NotificationsException(NotificationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun registerListener(id: String, listener: NotificationsListener) {
        notificationsListeners[id] = listener
    }

    override fun unregisterListener(id: String) {
        notificationsListeners.remove(id)
    }

    private fun hasNotification(notificationId: Int) : Boolean {
        return getNotification(notificationId) != null
    }

    private fun getNotification(notificationId: Int) : Notification? {
        for (page in _pages) {
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
        for (page in _pages) {
            page.notifications?.let { notifications ->
                for (notification in notifications) {
                    if (notification.id == notificationId) {
                        if (notification.state == NotificationState.INACTIVE || notification.state == NotificationState.UNREAD) {
                            notification.state = NotificationState.ACTIVE
                        } else {
                            notification.state = NotificationState.INACTIVE
                        }
                    } else if (notification.state == NotificationState.ACTIVE) {
                        notification.state = NotificationState.INACTIVE
                    }
                }
            }
        }
    }

    private fun toNotification(notificationResponse: NotificationResponse) : Notification {
        val state = if (notificationResponse.isRead == true) NotificationState.INACTIVE else NotificationState.UNREAD
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

    private fun toNotificationsPage(notificationsResponse: NotificationsResponse) : NotificationsPage {
        var notifications: List<Notification> = listOf()
        var isLastPage: Boolean = false

        if (notificationsResponse.data != null) {
            notifications = notificationsResponse.data!!.map { nr -> toNotification(nr) }
        }
        if (notificationsResponse.pageData?.currentPage != null && notificationsResponse.pageData?.lastPage != null) {
            isLastPage = notificationsResponse.pageData?.currentPage == notificationsResponse.pageData?.lastPage
        }

        return NotificationsPage(notifications, isLastPage)
    }

    override fun loginCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        if (isSuccessful) {
            reset()
        }
    }

    override fun notificationsCountChanged() {
        reset()
        loadNextPage { _, _ ->
            for (listener in this.notificationsListeners.values) {
                listener.notificationsChanged()
            }
        }
    }

    override fun privateConversationsCountChanged() {}
    override fun thomannConversationsCountChanged() {}
}