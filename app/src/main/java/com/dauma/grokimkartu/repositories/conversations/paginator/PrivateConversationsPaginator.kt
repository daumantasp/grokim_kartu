package com.dauma.grokimkartu.repositories.conversations.paginator

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.conversations.ConversationsErrors
import com.dauma.grokimkartu.repositories.conversations.entities.ConversationPage
import kotlinx.coroutines.flow.StateFlow

interface PrivateConversationsPaginator {
    val pages: StateFlow<List<ConversationPage>>
    val conversationPartnerId: StateFlow<Int?>
    val conversationPartnersIcons: MutableMap<Int, Bitmap>
    suspend fun loadNextPage(): Result<ConversationPage?, ConversationsErrors?>
    fun setConversationPartnerId(id: Int?)
    fun clear()
}