package com.dauma.grokimkartu.repositories.conversations.entities

import java.sql.Timestamp

data class Conversation(
    var id: Int?,
    var isRead: Boolean?,
    var createdAt: Timestamp?,
    var lastMessage: Message?
)