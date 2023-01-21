package com.dauma.grokimkartu.data.auth.entities

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("device_id") var deviceId: String?
)