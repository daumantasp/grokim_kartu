package com.dauma.grokimkartu.data.conversations

import com.dauma.grokimkartu.data.conversations.entities.MessageResponse
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.data.conversations.entities.PostMessageRequest

interface ThomannConversationsDao {
    fun thomannMessages(thomannId: Int, page: Int, pageSize: Int, accessToken: String, onComplete: (MessagesResponse?, ConversationsDaoResponseStatus) -> Unit)
    fun postThomannMessage(thomannId: Int, postMessageRequest: PostMessageRequest, accessToken: String, onComplete: (MessageResponse?, ConversationsDaoResponseStatus) -> Unit)
}