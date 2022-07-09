package com.dauma.grokimkartu.repositories.conversations.paginator

import com.dauma.grokimkartu.data.conversations.PrivateConversationsDao
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.repositories.conversations.ConversationsErrors
import com.dauma.grokimkartu.repositories.conversations.ConversationsException

class PrivateConversationsPaginatorImpl(private val privateConversationsDao: PrivateConversationsDao)
    : PrivateConversationsPaginator {
    private var _pages: MutableList<MessagesResponse> = mutableListOf()
    private var _conversationPartnerId: Int? = null

    override val pages: List<MessagesResponse>
        get() = _pages

    override val pageSize: Int = 20

    override var conversationPartnerId: Int?
        get() = _conversationPartnerId
        set(value) {
            clear()
            _conversationPartnerId = value
        }

    override fun loadNextPage(accessToken: String, onComplete: (MessagesResponse?, ConversationsErrors?) -> Unit) {
        if (conversationPartnerId != null) {
            if (isLastLoaded() == false) {
                val nextPage = _pages.count() + 1
                privateConversationsDao.messages(conversationPartnerId!!, nextPage, pageSize, accessToken) { messagesResponse, conversationsDaoResponseStatus ->
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
            throw ConversationsException(ConversationsErrors.CONVERSATION_PARTNER_ID_NOT_SET)
        }
    }

    override fun clear() {
        _pages.clear()
    }

    private fun isLastLoaded(): Boolean {
        _pages.lastOrNull()?.pageData?.let { pageData ->
            return pageData.currentPage == pageData.lastPage
        }
        return false
    }
}