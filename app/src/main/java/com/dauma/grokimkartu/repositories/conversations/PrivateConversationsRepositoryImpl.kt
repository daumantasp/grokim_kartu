package com.dauma.grokimkartu.repositories.conversations

import com.dauma.grokimkartu.data.conversations.PrivateConversationsDao
import com.dauma.grokimkartu.data.conversations.entities.PostMessageRequest
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.conversations.entities.Conversation
import com.dauma.grokimkartu.repositories.conversations.entities.Message
import com.dauma.grokimkartu.repositories.conversations.entities.PostMessage
import com.dauma.grokimkartu.repositories.conversations.paginator.PrivateConversationsPaginator

class PrivateConversationsRepositoryImpl(
    private val privateConversationsDao: PrivateConversationsDao,
    playersDao: PlayersDao,
    override val paginator: PrivateConversationsPaginator,
    private val user: User
) : ConversationsRepository(playersDao, user, paginator.conversationPartnersIcons), PrivateConversationsRepository {
    override suspend fun conversations(): Result<List<Conversation>?, ConversationsErrors?> {
        if (user.isUserLoggedIn()) {
            val response = privateConversationsDao.conversations(user.getBearerAccessToken()!!)
            val status = response.status
            val conversationsResponse = response.data
            if (status.isSuccessful && conversationsResponse != null) {
                val conversationsList = conversationsResponse.map { car -> toConversation(car) }
                return Result(conversationsList, null)
            } else {
                return Result(null, ConversationsErrors.UNKNOWN)
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun postMessage(postMessage: PostMessage): Result<Message?, ConversationsErrors?> {
        if (user.isUserLoggedIn()) {
            if (paginator.conversationPartnerId.value != null) {
                val postMessageRequest = PostMessageRequest(
                    text = postMessage.text
                )
                val response = privateConversationsDao.postMessage(
                    paginator.conversationPartnerId.value!!,
                    postMessageRequest,
                    user.getBearerAccessToken()!!
                )
                val status = response.status
                val messageResponse = response.data
                if (status.isSuccessful && messageResponse != null) {
                    val message = toMessage(messageResponse)
                    return Result(message, null)
                } else {
                    return Result(null, ConversationsErrors.UNKNOWN)
                }
            } else {
                throw ConversationsException(ConversationsErrors.CONVERSATION_PARTNER_ID_NOT_SET)
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }
}