package com.dauma.grokimkartu.repositories.conversations

import com.dauma.grokimkartu.data.conversations.PrivateConversationsDao
import com.dauma.grokimkartu.data.conversations.entities.MessageResponse
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.conversations.entities.ConversationPage
import com.dauma.grokimkartu.repositories.conversations.entities.Message
import com.dauma.grokimkartu.repositories.conversations.entities.MessageUser
import com.dauma.grokimkartu.repositories.conversations.paginator.PrivateConversationsPaginator

class PrivateConversationsRepositoryImpl(
    private val privateConversationsDao: PrivateConversationsDao,
    private val paginator: PrivateConversationsPaginator,
    private val user: User,
    private val utils: Utils
) : PrivateConversationsRepository {
    private val _pages: MutableList<ConversationPage> = mutableListOf()
    private var _conversationPartnerId: Int? = null

    override val pages: List<ConversationPage>
        get() = _pages

    override var conversationPartnerId: Int?
        get() = _conversationPartnerId
        set(value) {
            reset()
            _conversationPartnerId = value
        }

    override fun loadNextPage(onComplete: (ConversationPage?, ConversationsErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            if (conversationPartnerId != null) {
                paginator.loadNextPage(user.getBearerAccessToken()!!) { messagesResponse, conversationsErrors ->
                    if (messagesResponse != null) {
                        val conversationPage = toConversationPage(messagesResponse)
                        _pages.add(conversationPage)
                        onComplete(conversationPage, null)
                    } else {
                        onComplete(null, ConversationsErrors.UNKNOWN)
                    }
                }
            } else {
                throw ConversationsException(ConversationsErrors.CONVERSATION_PARTNER_ID_NOT_SET)
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun reset() {
        if (user.isUserLoggedIn()) {
            _pages.clear()
            paginator.clear()
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun toMessage(messageResponse: MessageResponse) : Message {
        return Message(
            id = messageResponse.id,
            user = MessageUser(
                id = messageResponse.user?.id,
                name = messageResponse.user?.name
            ),
            conversationId = messageResponse.conversationId,
            text = messageResponse.text,
            createdAt = messageResponse.createdAt
        )
    }

    private fun toConversationPage(messagesResponse: MessagesResponse) : ConversationPage {
        var messages: List<Message> = listOf()
        var isLastPage: Boolean = false

        if (messagesResponse.data != null) {
            messages = messagesResponse.data!!.map { mr -> toMessage(mr) }
        }
        if (messagesResponse.pageData?.currentPage != null && messagesResponse.pageData?.lastPage != null) {
            isLastPage = messagesResponse.pageData?.currentPage == messagesResponse.pageData?.lastPage
        }

        return ConversationPage(messages, isLastPage)
    }
}