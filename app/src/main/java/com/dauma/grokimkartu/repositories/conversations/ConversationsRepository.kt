package com.dauma.grokimkartu.repositories.conversations

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.conversations.entities.ConversationResponse
import com.dauma.grokimkartu.data.conversations.entities.MessageResponse
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.PlayersDaoResponseStatus
import com.dauma.grokimkartu.general.IconLoader
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.conversations.entities.*
import com.dauma.grokimkartu.repositories.players.PlayersErrors
import com.dauma.grokimkartu.repositories.Result

abstract class ConversationsRepository(
    private val playersDao: PlayersDao,
    private val user: User,
    private val conversationPartnersIcons: MutableMap<Int, Bitmap>
) {
    private suspend fun playerIcon(userId: Int): Result<Bitmap?, PlayersErrors?> {
        if (user.isUserLoggedIn()) {
            if (conversationPartnersIcons.containsKey(userId)) {
                return Result(conversationPartnersIcons[userId], null)
            } else {
                val response = playersDao.playerIcon(userId, user.getBearerAccessToken()!!)
                val status = response.status
                val playerIcon = response.data
                if (playerIcon != null) {
                    conversationPartnersIcons[userId] = playerIcon
                    return Result(playerIcon, null)
                } else {
                    val error: PlayersErrors
                    when (status.error) {
                        PlayersDaoResponseStatus.Errors.ICON_NOT_FOUND -> {
                            error = PlayersErrors.ICON_NOT_FOUND
                        }
                        else -> {
                            error = PlayersErrors.UNKNOWN
                        }
                    }
                    return Result(null, error)
                }
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    protected fun toMessage(messageResponse: MessageResponse) : Message {
        val iconDownload: suspend ((Bitmap?) -> Unit) -> Unit = { onComplete: (Bitmap?) -> Unit ->
            val response = this.playerIcon(messageResponse.user?.id ?: -1)
            onComplete(response.data)
        }
        return Message(
            id = messageResponse.id,
            user = MessageUser(
                id = messageResponse.user?.id,
                name = messageResponse.user?.name,
                isCurrent = messageResponse.user?.isCurrent,
                iconLoader = IconLoader(iconDownload)
            ),
            conversationId = messageResponse.conversationId,
            text = messageResponse.text,
            createdAt = messageResponse.createdAt
        )
    }

    protected fun toConversation(conversationResponse: ConversationResponse) : Conversation {
        var lastMessage: Message? = null
        conversationResponse.lastMessage?.let {
            lastMessage = toMessage(it)
        }
        val iconDownload: suspend ((Bitmap?) -> Unit) -> Unit = { onComplete: (Bitmap?) -> Unit ->
            val response = this.playerIcon(conversationResponse.partner?.id ?: -1)
            onComplete(response.data)
        }
        return Conversation(
            id = conversationResponse.id,
            isRead = conversationResponse.isRead,
            createdAt = conversationResponse.createdAt,
            lastMessage = lastMessage,
            partner = ConversationPartner(
                id = conversationResponse.partner?.id,
                name = conversationResponse.partner?.name,
                iconLoader = IconLoader(iconDownload)
            ),
            thomannId = conversationResponse.thomannId
        )
    }

    protected fun toConversationPage(messagesResponse: MessagesResponse) : ConversationPage {
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