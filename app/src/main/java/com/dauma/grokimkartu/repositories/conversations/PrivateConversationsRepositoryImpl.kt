package com.dauma.grokimkartu.repositories.conversations

import android.graphics.Bitmap
import android.util.Log
import com.dauma.grokimkartu.data.conversations.PrivateConversationsDao
import com.dauma.grokimkartu.data.conversations.entities.MessageResponse
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.data.conversations.entities.PostMessageRequest
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.PlayersDaoResponseStatus
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.conversations.entities.*
import com.dauma.grokimkartu.repositories.conversations.paginator.PrivateConversationsPaginator
import com.dauma.grokimkartu.repositories.players.PlayersErrors

class PrivateConversationsRepositoryImpl(
    private val privateConversationsDao: PrivateConversationsDao,
    private val playersDao: PlayersDao,
    private val paginator: PrivateConversationsPaginator,
    private val user: User,
    private val utils: Utils
) : PrivateConversationsRepository {
    private val _pages: MutableList<ConversationPage> = mutableListOf()
    private var _conversationPartnerId: Int? = null
    private var conversationPartnerIcon: Bitmap? = null
    private var isReloadInProgress: Boolean = false
    private val conversationListeners: MutableMap<String, ConversationListener> = mutableMapOf()

    override val pages: List<ConversationPage>
        get() = _pages

    override var conversationPartnerId: Int?
        get() = _conversationPartnerId
        set(value) {
            reset()
            if (value != null) {
                paginator.conversationPartnerId = value
                _conversationPartnerId = value
                reloadConversationPeriodically()
            }
        }

    companion object {
        private const val CONVERSATION_PERIODIC_RELOAD = "CONVERSATION_PERIODIC_RELOAD"
    }

    private fun reloadConversationPeriodically() {
        utils.dispatcherUtils.main.periodic(
            operationKey = CONVERSATION_PERIODIC_RELOAD,
            period = 1.0,
            startImmediately = true,
            repeats = true
        ) {
            Log.d("PrivateConversationsRepositoryImpl", "reloadConversationPeriodically")
            try {
                isConversationReloadNeeded { isNeeded ->
                    Log.d("PrivateConversationsRepositoryImpl", "isConversationReloadNeeded isNeeded=$isNeeded")
                    if (isNeeded) {
                        reload { conversationPage, conversationsErrors ->
                            notifyListeners()
                        }
                    }
                }
            } catch (e: ConversationsException) {}
        }
    }

    override fun loadNextPage(onComplete: (ConversationPage?, ConversationsErrors?) -> Unit) {
        Log.d("PrivateConversationsRepositoryImpl", "loadNextPage")
        if (user.isUserLoggedIn()) {
            if (conversationPartnerId != null) {
                isReloadInProgress = true
                paginator.loadNextPage(user.getBearerAccessToken()!!) { messagesResponse, conversationsErrors ->
                    if (messagesResponse != null) {
                        val conversationPage = toConversationPage(messagesResponse)
                        _pages.add(conversationPage)
                        Log.d("PrivateConversationsRepositoryImpl", "loadNextPage completed")
                        onComplete(conversationPage, null)
                    } else {
                        onComplete(null, ConversationsErrors.UNKNOWN)
                    }
                    isReloadInProgress = false
                }
            } else {
                throw ConversationsException(ConversationsErrors.CONVERSATION_PARTNER_ID_NOT_SET)
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun postMessage(
        postMessage: PostMessage,
        onComplete: (Message?, ConversationsErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            if (conversationPartnerId != null) {
                val postMessageRequest = PostMessageRequest(
                    text = postMessage.text
                )
                privateConversationsDao.postMessage(
                    conversationPartnerId!!,
                    postMessageRequest,
                    user.getBearerAccessToken()!!
                ) { messageResponse, conversationsDaoResponseStatus ->
                    if (conversationsDaoResponseStatus.isSuccessful && messageResponse != null) {
                        val message = toMessage(messageResponse)
                        onComplete(message, null)
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

    override fun registerListener(id: String, listener: ConversationListener) {
        conversationListeners[id] = listener
    }

    override fun unregisterListener(id: String) {
        conversationListeners.remove(id)
    }

    private fun loadLastMessage(onComplete: (Message?, ConversationsErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            if (conversationPartnerId != null) {
                privateConversationsDao.messages(
                    conversationPartnerId = conversationPartnerId!!,
                    page = 1,
                    pageSize = 1,
                    accessToken = user.getBearerAccessToken()!!
                ) { messagesResponse, conversationsDaoResponseStatus ->
                    if (conversationsDaoResponseStatus.isSuccessful && messagesResponse != null) {
                        if (messagesResponse.data?.isEmpty() == false) {
                            val messageResponse = messagesResponse.data!![0]
                            val message = toMessage(messageResponse)
                            onComplete(message, null)
                        } else {
                            onComplete(null, null)
                        }
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

    private fun isConversationReloadNeeded(onComplete: (Boolean) -> Unit) {
        if (user.isUserLoggedIn()) {
            if (conversationPartnerId != null) {
                if (isReloadInProgress == false) {
                    loadLastMessage { message, conversationsErrors ->
                        val previousLastMessage = _pages.firstOrNull()?.messages?.firstOrNull()
                        val isReloadNeeded = message?.id != previousLastMessage?.id
                        onComplete(isReloadNeeded)
                    }
                }
            } else {
                throw ConversationsException(ConversationsErrors.CONVERSATION_PARTNER_ID_NOT_SET)
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun reload(onComplete: (ConversationPage?, ConversationsErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            if (conversationPartnerId != null) {
                if (isReloadInProgress == false) {
                    isReloadInProgress = true

                    _pages.clear()
                    paginator.clear()
                    paginator.loadNextPage(user.getBearerAccessToken()!!) { messagesResponse, conversationsErrors ->
                        if (messagesResponse != null) {
                            val conversationPage = toConversationPage(messagesResponse)
                            _pages.add(conversationPage)
                            onComplete(conversationPage, null)
                        } else {
                            onComplete(null, ConversationsErrors.UNKNOWN)
                        }
                        isReloadInProgress = false
                    }
                }
            } else {
                throw ConversationsException(ConversationsErrors.CONVERSATION_PARTNER_ID_NOT_SET)
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun playerIcon(onComplete: (Bitmap?, PlayersErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            if (conversationPartnerIcon != null) {
                onComplete(conversationPartnerIcon, null)
            } else if (conversationPartnerId != null) {
                playersDao.playerIcon(conversationPartnerId!!, user.getBearerAccessToken()!!) { playerIcon, playersDaoResponseStatus ->
                    if (playerIcon != null) {
                        this.conversationPartnerIcon = playerIcon
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
            conversationPartnerIcon = null
            utils.dispatcherUtils.main.cancelPeriodic(CONVERSATION_PERIODIC_RELOAD)
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun toMessage(messageResponse: MessageResponse) : Message {
        val loader = { onComplete: (Bitmap?, PlayersErrors?) -> Unit ->
            this.playerIcon(onComplete)
        }
        return Message(
            id = messageResponse.id,
            user = MessageUser(
                id = messageResponse.user?.id,
                name = messageResponse.user?.name,
                isCurrent = messageResponse.user?.isCurrent,
                userIcon = MessageUserIcon(loader)
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

    private fun notifyListeners() {
        for (listener in this.conversationListeners.values) {
            listener.conversationChanged()
        }
    }
}