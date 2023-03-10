package com.dauma.grokimkartu.repositories.conversations.paginator

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.conversations.entities.MessageResponse
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.PlayersDaoResponseStatus
import com.dauma.grokimkartu.general.IconLoader
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.conversations.ConversationsErrors
import com.dauma.grokimkartu.repositories.conversations.ConversationsException
import com.dauma.grokimkartu.repositories.conversations.entities.ConversationPage
import com.dauma.grokimkartu.repositories.conversations.entities.Message
import com.dauma.grokimkartu.repositories.conversations.entities.MessageUser
import com.dauma.grokimkartu.repositories.players.PlayersErrors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class ConversationsPaginator(
    private val playersDao: PlayersDao,
    private val user: User
) {
    protected var _pages: MutableStateFlow<List<ConversationPage>> = MutableStateFlow(mutableListOf())
    val pages: StateFlow<List<ConversationPage>> = _pages.asStateFlow()

    protected val pageSize: Int = 20
    val conversationPartnersIcons: MutableMap<Int, Bitmap> = mutableMapOf()

    protected var isReloadInProgress: Boolean = false

    fun clear() {
        _pages.value = mutableListOf()
    }

    protected fun isLastLoaded(): Boolean {
        _pages.value.lastOrNull()?.let { pageValue ->
            return pageValue.isLast
        }
        return false
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

    private suspend fun playerIcon(userId: Int): Result<Bitmap?, PlayersErrors?> {
        if (user.isUserLoggedIn()) {
            if (conversationPartnersIcons.containsKey(userId)) {
                return Result(conversationPartnersIcons[userId], null)
            } else {
                val response = playersDao.playerIcon(userId, user.getBearerAccessToken()!!)
                val status = response.status
                val playerIcon = response.data
                if (playerIcon != null) {
                    this.conversationPartnersIcons[userId] = playerIcon
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
}