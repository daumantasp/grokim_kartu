package com.dauma.grokimkartu.repositories.conversations.paginator

import com.dauma.grokimkartu.data.conversations.ThomannConversationsDao
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.repositories.conversations.ConversationsErrors
import com.dauma.grokimkartu.repositories.conversations.ConversationsException

class ThomannConversationsPaginatorImpl(private val thomannConversationsDao: ThomannConversationsDao)
    : ConversationsPaginator(), ThomannConversationsPaginator {
    private var _thomannId: Int? = null
    override var thomannId: Int?
        get() = _thomannId
        set(value) {
            clear()
            _thomannId = value
        }

    override fun loadNextPage(accessToken: String, onComplete: (MessagesResponse?, ConversationsErrors?) -> Unit) {
        if (thomannId != null) {
            if (isLastLoaded() == false) {
                val nextPage = _pages.count() + 1
                thomannConversationsDao.thomannMessages(_thomannId!!, nextPage, pageSize, accessToken) { messagesResponse, conversationsDaoResponseStatus ->
                    if (conversationsDaoResponseStatus.isSuccessful && messagesResponse != null) {
                        _pages.add(messagesResponse)
                        onComplete(messagesResponse, null)
                    } else {
                        onComplete(null, ConversationsErrors.UNKNOWN)
                    }
                }
            } else {
                onComplete(_pages.lastOrNull(), null)
            }
        } else {
            throw ConversationsException(ConversationsErrors.THOMANN_ID_NOT_SET)
        }
    }
}