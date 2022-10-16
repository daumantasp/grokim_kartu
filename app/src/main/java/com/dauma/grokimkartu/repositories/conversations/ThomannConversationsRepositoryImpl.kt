package com.dauma.grokimkartu.repositories.conversations

import android.graphics.Bitmap
import android.util.Log
import com.dauma.grokimkartu.data.conversations.ThomannConversationsDao
import com.dauma.grokimkartu.data.conversations.entities.ConversationResponse
import com.dauma.grokimkartu.data.conversations.entities.MessageResponse
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.data.conversations.entities.PostMessageRequest
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.PlayersDaoResponseStatus
import com.dauma.grokimkartu.general.IconLoader
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.conversations.entities.*
import com.dauma.grokimkartu.repositories.conversations.paginator.ThomannConversationsPaginator
import com.dauma.grokimkartu.repositories.players.PlayersErrors

class ThomannConversationsRepositoryImpl(
    private val thomannConversationsDao: ThomannConversationsDao,
    private val playersDao: PlayersDao,
    private val paginator: ThomannConversationsPaginator,
    private val user: User,
    private val utils: Utils
) : ThomannConversationsRepository {
    private val _pages: MutableList<ConversationPage> = mutableListOf()
    private var _thomannId: Int? = null
    private var conversationPartnersIcons: MutableMap<Int, Bitmap> = mutableMapOf()
    private var isReloadInProgress: Boolean = false
    private val conversationListeners: MutableMap<String, ConversationListener> = mutableMapOf()

    override val pages: List<ConversationPage>
        get() = _pages

    override var thomannId: Int?
        get() = _thomannId
        set(value) {
            reset()
            if (value != null) {
                paginator.thomannId = value
                _thomannId = value
                reloadConversationPeriodically()
            }
        }

    companion object {
        private const val CONVERSATION_PERIODIC_RELOAD = "CONVERSATION_PERIODIC_RELOAD"
    }

    override fun thomannConversations(onComplete: (List<Conversation>?, ConversationsErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            thomannConversationsDao.thomannConversations(user.getBearerAccessToken()!!) { conversationArrayResponse, conversationsDaoResponseStatus ->
                if (conversationsDaoResponseStatus.isSuccessful && conversationArrayResponse != null) {
                    val conversationList = conversationArrayResponse.map { car -> toConversation(car) }
                    onComplete(conversationList, null)
                } else {
                    onComplete(null, ConversationsErrors.UNKNOWN)
                }
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
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
            if (thomannId != null) {
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
            if (thomannId != null) {
                val postMessageRequest = PostMessageRequest(
                    text = postMessage.text
                )
                thomannConversationsDao.postThomannMessage(
                    thomannId!!,
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
            if (thomannId != null) {
                thomannConversationsDao.thomannMessages(
                    thomannId = thomannId!!,
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
            if (thomannId != null) {
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
            if (thomannId != null) {
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

    private fun playerIcon(userId: Int, onComplete: (Bitmap?, PlayersErrors?) -> Unit) {
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

    private fun reset() {
        if (user.isUserLoggedIn()) {
            _pages.clear()
            paginator.clear()
            conversationPartnersIcons.clear()
            utils.dispatcherUtils.main.cancelPeriodic(CONVERSATION_PERIODIC_RELOAD)
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun toMessage(messageResponse: MessageResponse) : Message {
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

    private fun toConversation(conversationResponse: ConversationResponse) : Conversation {
        var lastMessage: Message? = null
        conversationResponse.lastMessage?.let {
            lastMessage = toMessage(it)
        }
        return Conversation(
            id = conversationResponse.id,
            isRead = conversationResponse.isRead,
            createdAt = conversationResponse.createdAt,
            lastMessage = lastMessage
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