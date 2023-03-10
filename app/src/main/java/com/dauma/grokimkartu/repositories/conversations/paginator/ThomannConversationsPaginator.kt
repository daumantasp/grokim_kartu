package com.dauma.grokimkartu.repositories.conversations.paginator

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.conversations.ConversationsErrors
import com.dauma.grokimkartu.repositories.conversations.entities.ConversationPage
import kotlinx.coroutines.flow.StateFlow

interface ThomannConversationsPaginator {
    val pages: StateFlow<List<ConversationPage>>
    val thomannId: StateFlow<Int?>
    val conversationPartnersIcons: MutableMap<Int, Bitmap>
    suspend fun loadNextPage(): Result<ConversationPage?, ConversationsErrors?>
    fun setThomannId(id: Int?)
    fun clear()
}