package com.dauma.grokimkartu.data.notifications.entities

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class NotificationResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("user") var user: NotificationUserConciseResponse?,
    @SerializedName("is_read") var isRead: Boolean?,
    @SerializedName("name") var name: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("created_at") var createdAt: Timestamp?
)