package com.dauma.grokimkartu.repositories.conversations

import com.dauma.grokimkartu.repositories.conversations.entities.ConversationPage

interface PrivateConversationsRepository {
    val pages: List<ConversationPage>
    var conversationPartnerId: Int?
    fun loadNextPage(onComplete: (ConversationPage?, ConversationsErrors?) -> Unit)
}