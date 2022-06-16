package com.dauma.grokimkartu.data.notifications.entities

import com.google.gson.annotations.SerializedName

data class NotificationUserConciseResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?
)