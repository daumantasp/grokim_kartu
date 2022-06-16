package com.dauma.grokimkartu.data.notifications

import com.dauma.grokimkartu.data.notifications.entities.NotificationResponse
import com.dauma.grokimkartu.data.notifications.entities.NotificationsResponse
import com.dauma.grokimkartu.data.notifications.entities.UpdateNotificationRequest

interface NotificationsDao {
    fun notifications(page: Int, pageSize: Int, accessToken: String, onComplete: (NotificationsResponse?, NotificationsDaoResponseStatus) -> Unit)
    fun unreadCount(accessToken: String, onComplete: (Int?, NotificationsDaoResponseStatus) -> Unit)
    fun update(notificationId: Int, updateRequest: UpdateNotificationRequest, accessToken: String, onComplete: (NotificationResponse?, NotificationsDaoResponseStatus) -> Unit)
}