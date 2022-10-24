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

abstract class ConversationsRepository(
    private val playersDao: PlayersDao,
    private val user: User
) {
    protected val _pages: MutableList<ConversationPage> = mutableListOf()
    protected var conversationPartnersIcons: MutableMap<Int, Bitmap> = mutableMapOf()
    protected var isReloadInProgress: Boolean = false
    protected val conversationListeners: MutableMap<String, ConversationListener> = mutableMapOf()

    val pages: List<ConversationPage>
        get() = _pages

    fun registerListener(id: String, listener: ConversationListener) {
        conversationListeners[id] = listener
    }

    fun unregisterListener(id: String) {
        conversationListeners.remove(id)
    }

    protected fun playerIcon(userId: Int, onComplete: (Bitmap?, PlayersErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            if (conversationPartnersIcons.containsKey(userId)) {
                onComplete(conversationPartnersIcons[userId], null)
            } else {
                playersDao.playerIcon(userId, user.getBearerAccessToken()!!) { playerIcon, playersDaoResponseStatus ->
                    if (playerIcon != null) {
                        this.conversationPartnersIcons[userId] = playerIcon
                        onComplete(playerIcon, null)
                    } else {
                        val error: PlayersErrors
                        when (playersDaoResponseStatus.error) {
                            PlayersDaoResponseStatus.Errors.ICON_NOT_FOUND -> {
                                error = PlayersErrors.ICON_NOT_FOUND
                            }
                            else -> {
                                error = PlayersErrors.UNKNOWN
                            }
                        }
                        onComplete(null, error)
                    }
                }
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    protected fun toMessage(messageResponse: MessageResponse) : Message {
        val iconDownload: ((Bitmap?) -> Unit) -> Unit = { onComplete: (Bitmap?) -> Unit ->
            this.playerIcon(messageResponse.user?.id ?: -1) { icon, _ ->
                onComplete(icon)
            }
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
        val iconDownload: ((Bitmap?) -> Unit) -> Unit = { onComplete: (Bitmap?) -> Unit ->
            this.playerIcon(conversationResponse.partner?.id ?: -1) { icon, _ ->
                onComplete(icon)
            }
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

    protected fun notifyListeners() {
        for (listener in this.conversationListeners.values) {
            listener.conversationChanged()
        }
    }
}