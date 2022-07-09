package com.dauma.grokimkartu.repositories.conversations

class ConversationsException(error: ConversationsErrors)
    : Exception(error.toString()) {}


enum class ConversationsErrors {
    CONVERSATION_PARTNER_ID_NOT_SET,
    THOMANN_ID_NOT_SET,
    UNKNOWN
}