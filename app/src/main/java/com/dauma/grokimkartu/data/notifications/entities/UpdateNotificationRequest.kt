package com.dauma.grokimkartu.data.notifications.entities

import com.google.gson.annotations.SerializedName

data class UpdateNotificationRequest(
    @SerializedName("is_read") var isRead: Boolean
)
