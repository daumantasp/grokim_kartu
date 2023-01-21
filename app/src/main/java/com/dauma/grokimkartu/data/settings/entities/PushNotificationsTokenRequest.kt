package com.dauma.grokimkartu.data.settings.entities

import com.google.gson.annotations.SerializedName

data class PushNotificationsTokenRequest(
    @SerializedName("token") var token: String?,
    @SerializedName("device_id") var deviceId: String?
)