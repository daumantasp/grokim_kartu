package com.dauma.grokimkartu.data.thomanns.entities

import com.google.gson.annotations.SerializedName

data class KickThomannRequest(
    @SerializedName("user_id") var userId: Int?
)
