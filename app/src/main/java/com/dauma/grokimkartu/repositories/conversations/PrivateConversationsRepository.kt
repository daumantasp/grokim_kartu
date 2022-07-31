package com.dauma.grokimkartu.repositories.conversations

import com.dauma.grokimkartu.repositories.conversations.entities.Conversation
import com.dauma.grokimkartu.repositories.conversations.entities.ConversationPage
import com.dauma.grokimkartu.repositories.conversations.entities.Message
import com.dauma.grokimkartu.repositories.conversations.entities.PostMessage

interface PrivateConversationsRepository {
    val pages: List<ConversationPage>
    var conversationPartnerId: Int?
    fun conversations(onComplete: (List<Conversation>?, ConversationsErrors?) -> Unit)
    fun loadNextPage(onComplete: (ConversationPage?, ConversationsErrors?) -> Unit)
    fun postMessage(postMessage: PostMessage, onComplete: (Message?, ConversationsErrors?) -> Unit)
    fun registerListener(id: String, listener: ConversationListener)
    fun unregisterListener(id: String)
}