package com.dauma.grokimkartu.repositories.conversations.entities

data class ConversationPage (
    val messages: List<Message>?,
    val isLast: Boolean
)