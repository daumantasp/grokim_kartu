package com.dauma.grokimkartu.data.notifications

import com.dauma.grokimkartu.data.notifications.entities.NotificationResponse
import com.dauma.grokimkartu.data.notifications.entities.NotificationsResponse
import com.dauma.grokimkartu.data.notifications.entities.UpdateNotificationRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class NotificationsDaoImpl(retrofit: Retrofit) : NotificationsDao {
    private val retrofitNotifications: RetrofitNotifications = retrofit.create(RetrofitNotifications::class.java)

    override fun notifications(
        page: Int,
        pageSize: Int,
        accessToken: String,
        onComplete: (NotificationsResponse?, NotificationsDaoResponseStatus) -> Unit
    ) {
        retrofitNotifications.notifications(page, pageSize, accessToken).enqueue(object : Callback<NotificationsResponse> {
            override fun onResponse(
                call: Call<NotificationsResponse>,
                response: Response<NotificationsResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val notificationsResponse = response.body()
                        val status = NotificationsDaoResponseStatus(true, null)
                        onComplete(notificationsResponse, status)
                    }
                    else -> {
                        val status = NotificationsDaoResponseStatus(false, NotificationsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                val status = NotificationsDaoResponseStatus(false, NotificationsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun update(
        notificationId: Int,
        updateRequest: UpdateNotificationRequest,
        accessToken: String,
        onComplete: (NotificationResponse?, NotificationsDaoResponseStatus) -> Unit
    ) {
        retrofitNotifications.updateNotification(accessToken, notificationId, updateRequest).enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(
                call: Call<NotificationResponse>,
                response: Response<NotificationResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val notificationResponse = response.body()
                        val status = NotificationsDaoResponseStatus(true, null)
                        onComplete(notificationResponse, status)
                    }
                    403 -> {
                        val status = NotificationsDaoResponseStatus(false, NotificationsDaoResponseStatus.Errors.FORBIDDEN)
                        onComplete(null, status)
                    }
                    404 -> {
                        val status = NotificationsDaoResponseStatus(false, NotificationsDaoResponseStatus.Errors.NOTIFICATION_NOT_FOUND)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = NotificationsDaoResponseStatus(false, NotificationsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                val status = NotificationsDaoResponseStatus(false, NotificationsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    private interface RetrofitNotifications {
        @GET("notifications")
        fun notifications(
            @Query("page") page: Int,
            @Query("page_size") pageSize: Int,
            @Header("Authorization") accessToken: String
        ) : Call<NotificationsResponse>

        @PUT("notifications") fun updateNotification(@Header("Authorization") accessToken: String, @Query("id") id: Int, @Body updateRequest: UpdateNotificationRequest) : Call<NotificationResponse>
    }
}