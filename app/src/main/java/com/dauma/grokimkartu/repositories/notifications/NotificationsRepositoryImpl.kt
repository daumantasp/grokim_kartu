package com.dauma.grokimkartu.repositories.notifications

import com.dauma.grokimkartu.data.notifications.NotificationsDao
import com.dauma.grokimkartu.data.notifications.entities.NotificationResponse
import com.dauma.grokimkartu.data.notifications.entities.NotificationsResponse
import com.dauma.grokimkartu.data.notifications.entities.UpdateNotificationRequest
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.notifications.entities.Notification
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationUserConcise
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationsPage
import com.dauma.grokimkartu.repositories.notifications.entities.UpdateNotification
import com.dauma.grokimkartu.repositories.notifications.paginator.NotificationsPaginator

class NotificationsRepositoryImpl(
    private val notificationsDao: NotificationsDao,
    private val paginator: NotificationsPaginator,
    private val user: User
) : NotificationsRepository {
    private val _pages: MutableList<NotificationsPage> = mutableListOf()

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

    override fun update(
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

    override fun reset() {
        if (user.isUserLoggedIn()) {
            _pages.clear()
            paginator.clear()
        } else {
            throw NotificationsException(NotificationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun toNotification(notificationResponse: NotificationResponse) : Notification {
        return Notification(
            id = notificationResponse.id,
            user = NotificationUserConcise(
                id = notificationResponse.user?.id,
                name = notificationResponse.user?.name
            ),
            isRead = notificationResponse.isRead,
            name = notificationResponse.name,
            description = notificationResponse.description,
            createdAt = notificationResponse.createdAt
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
}