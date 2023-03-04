package com.dauma.grokimkartu.data.conversations

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.conversations.entities.ConversationResponse
import com.dauma.grokimkartu.data.conversations.entities.MessageResponse
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.data.conversations.entities.PostMessageRequest

interface ThomannConversationsDao {
    suspend fun thomannConversations(accessToken: String): DaoResult<List<ConversationResponse>?, ConversationsDaoResponseStatus>
    suspend fun thomannMessages(thomannId: Int, page: Int, pageSize: Int, accessToken: String): DaoResult<MessagesResponse?, ConversationsDaoResponseStatus>
    suspend fun postThomannMessage(thomannId: Int, postMessageRequest: PostMessageRequest, accessToken: String): DaoResult<MessageResponse?, ConversationsDaoResponseStatus>
}