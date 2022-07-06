package com.dauma.grokimkartu.data.conversations

class ConversationsDaoResponseStatus (
    val isSuccessful: Boolean,
    val error: Errors?
) {
    enum class Errors {
        INVALID_USER_ID,
        INTERNAL_SERVER_ERROR,
        THOMANN_NOT_FOUND,
        FORBIDDEN,
        UNKNOWN
    }
}