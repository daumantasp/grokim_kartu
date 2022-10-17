package com.dauma.grokimkartu.repositories.conversations

import android.util.Log
import com.dauma.grokimkartu.data.conversations.ThomannConversationsDao
import com.dauma.grokimkartu.data.conversations.entities.PostMessageRequest
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.conversations.entities.Conversation
import com.dauma.grokimkartu.repositories.conversations.entities.ConversationPage
import com.dauma.grokimkartu.repositories.conversations.entities.Message
import com.dauma.grokimkartu.repositories.conversations.entities.PostMessage
import com.dauma.grokimkartu.repositories.conversations.paginator.ThomannConversationsPaginator

class ThomannConversationsRepositoryImpl(
    private val thomannConversationsDao: ThomannConversationsDao,
    playersDao: PlayersDao,
    private val paginator: ThomannConversationsPaginator,
    private val user: User,
    private val utils: Utils
) : ConversationsRepository(playersDao, user), ThomannConversationsRepository {
    private var _thomannId: Int? = null
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
}