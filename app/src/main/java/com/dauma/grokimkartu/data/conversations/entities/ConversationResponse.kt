package com.dauma.grokimkartu.data.conversations.entities

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class ConversationResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("is_read") var isRead: Boolean?,
    @SerializedName("created_at") var createdAt: Timestamp?,
    @SerializedName("last_message") var lastMessage: MessageResponse?,
    @SerializedName("partner") var partner: ConversationPartnerResponse?,
    @SerializedName("thomann_id") var thomannId: Int?
)