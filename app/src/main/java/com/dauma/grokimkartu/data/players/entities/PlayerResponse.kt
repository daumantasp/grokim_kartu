package com.dauma.grokimkartu.data.players.entities

import com.google.gson.annotations.SerializedName

data class PlayerResponse(
    @SerializedName("user_id") var userId: Int?,
    @SerializedName("name") var name: String?,
    @SerializedName("city") var city: String?,
    @SerializedName("instrument") var instrument: String?
)