package com.dauma.grokimkartu.repositories.conversations

import com.dauma.grokimkartu.data.conversations.ThomannConversationsDao
import com.dauma.grokimkartu.data.conversations.entities.PostMessageRequest
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.conversations.entities.Conversation
import com.dauma.grokimkartu.repositories.conversations.entities.Message
import com.dauma.grokimkartu.repositories.conversations.entities.PostMessage
import com.dauma.grokimkartu.repositories.conversations.paginator.ThomannConversationsPaginator

class ThomannConversationsRepositoryImpl(
    private val thomannConversationsDao: ThomannConversationsDao,
    playersDao: PlayersDao,
    override val paginator: ThomannConversationsPaginator,
    private val user: User
) : ConversationsRepository(playersDao, user, paginator.conversationPartnersIcons), ThomannConversationsRepository {
    override suspend fun thomannConversations(): Result<List<Conversation>?, ConversationsErrors?> {
        if (user.isUserLoggedIn()) {
            val response = thomannConversationsDao.thomannConversations(user.getBearerAccessToken()!!)
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
            if (paginator.thomannId.value != null) {
                val postMessageRequest = PostMessageRequest(
                    text = postMessage.text
                )
                val response = thomannConversationsDao.postThomannMessage(
                    paginator.thomannId.value!!,
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