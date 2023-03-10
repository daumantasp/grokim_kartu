package com.dauma.grokimkartu.repositories.conversations

import com.dauma.grokimkartu.repositories.conversations.entities.Conversation
import com.dauma.grokimkartu.repositories.conversations.entities.Message
import com.dauma.grokimkartu.repositories.conversations.entities.PostMessage
import com.dauma.grokimkartu.repositories.conversations.paginator.ThomannConversationsPaginator
import com.dauma.grokimkartu.repositories.Result

interface ThomannConversationsRepository {
    val paginator: ThomannConversationsPaginator
    suspend fun thomannConversations(): Result<List<Conversation>?, ConversationsErrors?>
    suspend fun postMessage(postMessage: PostMessage): Result<Message?, ConversationsErrors?>
}