package com.dauma.grokimkartu.repositories.notifications.paginator

import com.dauma.grokimkartu.data.notifications.NotificationsDao
import com.dauma.grokimkartu.data.notifications.entities.NotificationResponse
import com.dauma.grokimkartu.data.notifications.entities.NotificationsResponse
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.notifications.NotificationsErrors
import com.dauma.grokimkartu.repositories.notifications.NotificationsException
import com.dauma.grokimkartu.repositories.notifications.entities.Notification
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationState
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationUserConcise
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationsPage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationsPaginatorImpl(
    private val notificationsDao: NotificationsDao,
    private val user: User
) : NotificationsPaginator {
    private var _pages: MutableStateFlow<List<NotificationsPage>> = MutableStateFlow(mutableListOf())
    override val pages: StateFlow<List<NotificationsPage>> = _pages.asStateFlow()

    private val pageSize: Int = 20

    override suspend fun loadNextPage(): Result<NotificationsPage?, NotificationsErrors?> {
        if (user.isUserLoggedIn()) {
            if (isLastLoaded() == false) {
                val nextPage = _pages.value.count() + 1
                val response = notificationsDao.notifications(nextPage, pageSize, user.getBearerAccessToken()!!)
                val status = response.status
                val notificationsResponse = response.data
                if (status.isSuccessful && notificationsResponse != null) {
                    val notificationsPage = toNotificationsPage(notificationsResponse)
                    val pages = _pages.value.toMutableList()
                    pages.add(notificationsPage)
                    _pages.value = pages
                    return Result(notificationsPage, null)
                } else {
                    return Result(null, NotificationsErrors.UNKNOWN)
                }
            } else {
                return Result(_pages.value.lastOrNull(), null)
            }
        } else {
            throw NotificationsException(NotificationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun isLastLoaded(): Boolean {
        _pages.value.lastOrNull()?.let { pageValue ->
            return pageValue.isLast
        }
        return false
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

    override fun clear() {
        _pages.value = mutableListOf()
    }
}