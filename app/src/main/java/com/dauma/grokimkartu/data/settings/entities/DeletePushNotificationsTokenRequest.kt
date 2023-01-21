package com.dauma.grokimkartu.data.settings.entities

import com.google.gson.annotations.SerializedName

data class DeletePushNotificationsTokenRequest(
    @SerializedName("device_id") var deviceId: String?
)