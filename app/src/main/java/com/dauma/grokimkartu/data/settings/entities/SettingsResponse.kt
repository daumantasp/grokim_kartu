package com.dauma.grokimkartu.data.settings.entities

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class SettingsResponse(
    @SerializedName("name") var name: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("created_at") var createdAt: Timestamp?,
    @SerializedName("is_visible") var isVisible: Boolean?
)