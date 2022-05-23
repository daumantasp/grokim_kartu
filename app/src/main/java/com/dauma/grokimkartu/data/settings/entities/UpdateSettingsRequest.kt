package com.dauma.grokimkartu.data.settings.entities

import com.google.gson.annotations.SerializedName

data class UpdateSettingsRequest(
    @SerializedName("is_visible") var isVisible: Boolean?
)