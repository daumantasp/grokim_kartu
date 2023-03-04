package com.dauma.grokimkartu.data.notifications

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.notifications.entities.NotificationResponse
import com.dauma.grokimkartu.data.notifications.entities.NotificationsResponse
import com.dauma.grokimkartu.data.notifications.entities.UpdateNotificationRequest

interface NotificationsDao {
    suspend fun notifications(page: Int, pageSize: Int, accessToken: String): DaoResult<NotificationsResponse?, NotificationsDaoResponseStatus>
    suspend fun update(notificationId: Int, updateRequest: UpdateNotificationRequest, accessToken: String): DaoResult<NotificationResponse?, NotificationsDaoResponseStatus>
}