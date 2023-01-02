package com.dauma.grokimkartu.data.profile.entities

import com.google.gson.annotations.SerializedName

data class ProfileUnreadCountResponse(
    @SerializedName("unread_notifications_count") var unreadNotificationsCount: Int?,
    @SerializedName("unread_private_conversations_count") var unreadPrivateConversationsCount: Int?,
    @SerializedName("unread_thomann_conversations_count") var unreadThomannConversationsCount: Int?
)