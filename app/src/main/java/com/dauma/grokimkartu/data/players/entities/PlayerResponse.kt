package com.dauma.grokimkartu.data.players.entities

import com.google.gson.annotations.SerializedName

data class PlayerResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?,
    @SerializedName("city") var city: String?,
    @SerializedName("instrument") var instrument: String?
)