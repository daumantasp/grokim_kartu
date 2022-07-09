package com.dauma.grokimkartu.repositories.conversations.entities

import java.sql.Timestamp

data class Message(
    var id: Int?,
    var user: MessageUser?,
    var conversationId: Int?,
    var text: String?,
    var createdAt: Timestamp?
)