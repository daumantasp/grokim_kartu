package com.dauma.grokimkartu.data.notifications.entities

import com.google.gson.annotations.SerializedName

data class UnreadNotificationsResponse(
    @SerializedName("unread_count") var unreadCount: Int?,
)
