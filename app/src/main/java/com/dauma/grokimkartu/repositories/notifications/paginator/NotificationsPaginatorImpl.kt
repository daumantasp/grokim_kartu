package com.dauma.grokimkartu.repositories.notifications.paginator

import com.dauma.grokimkartu.data.notifications.NotificationsDao
import com.dauma.grokimkartu.data.notifications.entities.NotificationsResponse
import com.dauma.grokimkartu.repositories.notifications.NotificationsErrors

class NotificationsPaginatorImpl(private val notificationsDao: NotificationsDao)
    : NotificationsPaginator {
    private var _pages: MutableList<NotificationsResponse> = mutableListOf()
    override val pages: List<NotificationsResponse>
        get() = _pages
    override val pageSize: Int = 20

    override fun loadNextPage(accessToken: String, onComplete: (NotificationsResponse?, NotificationsErrors?) -> Unit) {
        if (isLastLoaded() == false) {
            val nextPage = _pages.count() + 1
            notificationsDao.notifications(nextPage, pageSize, accessToken) { notificationsResponse, notificationsDaoResponseStatus ->
                if (notificationsDaoResponseStatus.isSuccessful && notificationsResponse != null) {
                    _pages.add(notificationsResponse)
                    onComplete(notificationsResponse, null)
                } else {
                    onComplete(null, NotificationsErrors.UNKNOWN)
                }
            }
        } else {
            onComplete(_pages.lastOrNull(), null)
        }
    }

    override fun clear() {
        _pages.clear()
    }

    private fun isLastLoaded(): Boolean {
        _pages.lastOrNull()?.pageData?.let { pageData ->
            return pageData.currentPage == pageData.lastPage
        }
        return false
    }
}