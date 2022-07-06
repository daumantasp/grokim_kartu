package com.dauma.grokimkartu.data.conversations.entities

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class MessageResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("user") var user: MessageUserResponse?,
    @SerializedName("conversation_id") var conversationId: Int?,
    @SerializedName("text") var text: String?,
    @SerializedName("created_at") var createdAt: Timestamp?
)