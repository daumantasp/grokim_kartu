package com.dauma.grokimkartu.repositories.conversations.paginator

import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.repositories.conversations.ConversationsErrors

interface ThomannConversationsPaginator {
    val pages: List<MessagesResponse>
    val pageSize: Int
    var thomannId: Int?
    fun loadNextPage(accessToken: String, onComplete: (MessagesResponse?, ConversationsErrors?) -> Unit)
    fun clear()
}