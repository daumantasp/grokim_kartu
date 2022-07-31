package com.dauma.grokimkartu.data.conversations

import com.dauma.grokimkartu.data.conversations.entities.ConversationResponse
import com.dauma.grokimkartu.data.conversations.entities.MessageResponse
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.data.conversations.entities.PostMessageRequest

interface PrivateConversationsDao {
    fun conversations(accessToken: String, onComplete: (List<ConversationResponse>?, ConversationsDaoResponseStatus) -> Unit)
    fun messages(conversationPartnerId: Int, page: Int, pageSize: Int, accessToken: String, onComplete: (MessagesResponse?, ConversationsDaoResponseStatus) -> Unit)
    fun postMessage(conversationPartnerId: Int, postMessageRequest: PostMessageRequest, accessToken: String, onComplete: (MessageResponse?, ConversationsDaoResponseStatus) -> Unit)
}