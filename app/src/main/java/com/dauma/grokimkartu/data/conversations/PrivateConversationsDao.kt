package com.dauma.grokimkartu.data.conversations

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.conversations.entities.ConversationResponse
import com.dauma.grokimkartu.data.conversations.entities.MessageResponse
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.data.conversations.entities.PostMessageRequest

interface PrivateConversationsDao {
    suspend fun conversations(accessToken: String): DaoResult<List<ConversationResponse>?, ConversationsDaoResponseStatus>
    suspend fun messages(conversationPartnerId: Int, page: Int, pageSize: Int, accessToken: String): DaoResult<MessagesResponse?, ConversationsDaoResponseStatus>
    suspend fun postMessage(conversationPartnerId: Int, postMessageRequest: PostMessageRequest, accessToken: String): DaoResult<MessageResponse?, ConversationsDaoResponseStatus>
}