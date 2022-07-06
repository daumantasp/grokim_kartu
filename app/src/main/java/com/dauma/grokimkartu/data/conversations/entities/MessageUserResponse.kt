package com.dauma.grokimkartu.data.conversations.entities

import com.google.gson.annotations.SerializedName

data class MessageUserResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?
)