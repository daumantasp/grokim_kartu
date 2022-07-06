package com.dauma.grokimkartu.data.conversations.entities

import com.google.gson.annotations.SerializedName

data class PostMessageRequest(
    @SerializedName("text") var text: String?
)
