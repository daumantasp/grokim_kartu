package com.dauma.grokimkartu.data.notifications

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.notifications.entities.NotificationResponse
import com.dauma.grokimkartu.data.notifications.entities.NotificationsResponse
import com.dauma.grokimkartu.data.notifications.entities.UpdateNotificationRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class NotificationsDaoImpl(retrofit: Retrofit) : NotificationsDao {
    private val retrofitNotifications: RetrofitNotifications = retrofit.create(RetrofitNotifications::class.java)

    override suspend fun notifications(
        page: Int,
        pageSize: Int,
        accessToken: String
    ): DaoResult<NotificationsResponse?, NotificationsDaoResponseStatus> {
        val response = retrofitNotifications.notifications(page, pageSize, accessToken)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val notificationsResponse = response.body()
                    val status = NotificationsDaoResponseStatus(true, null)
                    return DaoResult(notificationsResponse, status)
                }
                else -> {
                    val status = NotificationsDaoResponseStatus(false, NotificationsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = NotificationsDaoResponseStatus(false, NotificationsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun update(
        notificationId: Int,
        updateRequest: UpdateNotificationRequest,
        accessToken: String
    ): DaoResult<NotificationResponse?, NotificationsDaoResponseStatus> {
        val response = retrofitNotifications.updateNotification(accessToken, notificationId, updateRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val notificationResponse = response.body()
                    val status = NotificationsDaoResponseStatus(true, null)
                    return DaoResult(notificationResponse, status)
                }
                403 -> {
                    val status = NotificationsDaoResponseStatus(false, NotificationsDaoResponseStatus.Errors.FORBIDDEN)
                    return DaoResult(null, status)
                }
                404 -> {
                    val status = NotificationsDaoResponseStatus(false, NotificationsDaoResponseStatus.Errors.NOTIFICATION_NOT_FOUND)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = NotificationsDaoResponseStatus(false, NotificationsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = NotificationsDaoResponseStatus(false, NotificationsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    private interface RetrofitNotifications {
        @GET("notifications")
        suspend fun notifications(
            @Query("page") page: Int,
            @Query("page_size") pageSize: Int,
            @Header("Authorization") accessToken: String
        ) : Response<NotificationsResponse>

        @PUT("notifications") 
        suspend fun updateNotification(@Header("Authorization") accessToken: String, @Query("id") id: Int, @Body updateRequest: UpdateNotificationRequest) : Response<NotificationResponse>
    }
}