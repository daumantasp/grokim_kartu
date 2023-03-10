package com.dauma.grokimkartu.repositories.conversations.paginator

import android.util.Log
import com.dauma.grokimkartu.data.conversations.PrivateConversationsDao
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.conversations.ConversationsErrors
import com.dauma.grokimkartu.repositories.conversations.ConversationsException
import com.dauma.grokimkartu.repositories.conversations.entities.ConversationPage
import com.dauma.grokimkartu.repositories.conversations.entities.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrivateConversationsPaginatorImpl(
    private val privateConversationsDao: PrivateConversationsDao,
    playersDao: PlayersDao,
    private val user: User,
    private val utils: Utils
    ) : ConversationsPaginator(playersDao, user), PrivateConversationsPaginator {
    private var _conversationPartnerId: MutableStateFlow<Int?> = MutableStateFlow(null)
    override val conversationPartnerId: StateFlow<Int?> = _conversationPartnerId.asStateFlow()

    companion object {
        private const val CONVERSATION_PERIODIC_RELOAD = "CONVERSATION_PERIODIC_RELOAD"
    }

    override suspend fun loadNextPage(): Result<ConversationPage?, ConversationsErrors?> {
        if (_conversationPartnerId.value != null) {
            if (isLastLoaded() == false) {
                val nextPage = _pages.value.count() + 1
                val response = privateConversationsDao.messages(conversationPartnerId.value!!, nextPage, pageSize, user.getBearerAccessToken()!!)
                val status = response.status
                val messagesResponse = response.data
                if (status.isSuccessful && messagesResponse != null) {
                    val conversationPage = toConversationPage(messagesResponse)
                    val pages = _pages.value.toMutableList()
                    _pages.value = pages
                    return Result(conversationPage, null)
                } else {
                    return Result(null, ConversationsErrors.UNKNOWN)
                }
            } else {
                return Result(_pages.value.lastOrNull(), null)
            }
        } else {
            throw ConversationsException(ConversationsErrors.CONVERSATION_PARTNER_ID_NOT_SET)
        }
    }

    override fun setConversationPartnerId(id: Int?) {
        clear()
        _conversationPartnerId.value = id
        utils.dispatcherUtils.main.cancelPeriodic(CONVERSATION_PERIODIC_RELOAD)
        reloadConversationPeriodically()
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
                CoroutineScope(Dispatchers.IO).launch {
                    val isNeeded = isConversationReloadNeeded()
                    if (isNeeded) {
                        reload()
                    }
                }
            } catch (e: ConversationsException) {}
        }
    }

    private suspend fun isConversationReloadNeeded(): Boolean {
        if (user.isUserLoggedIn()) {
            if (conversationPartnerId.value != null) {
                if (isReloadInProgress == false) {
                    val response = loadLastMessage()
                    val message = response.data
                    val previousLastMessage = _pages.value.firstOrNull()?.messages?.firstOrNull()
                    return message?.id != previousLastMessage?.id
                } else {
                    return false
                }
            } else {
                throw ConversationsException(ConversationsErrors.CONVERSATION_PARTNER_ID_NOT_SET)
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private suspend fun reload(): Result<ConversationPage?, ConversationsErrors?> {
        if (user.isUserLoggedIn()) {
            if (conversationPartnerId.value != null) {
                if (isReloadInProgress == false) {
                    isReloadInProgress = true
                    clear()
                    val response = loadNextPage()
                    val conversationPage = response.data
                    if (conversationPage != null) {
                        return Result(conversationPage, null)
                    } else {
                        return Result(null, ConversationsErrors.UNKNOWN)
                    }
                } else {
                    throw ConversationsException(ConversationsErrors.CONVERSATION_ALREADY_RELOADING)
                }
            } else {
                throw ConversationsException(ConversationsErrors.CONVERSATION_PARTNER_ID_NOT_SET)
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private suspend fun loadLastMessage(): Result<Message?, ConversationsErrors?> {
        if (user.isUserLoggedIn()) {
            if (conversationPartnerId.value != null) {
                val response = privateConversationsDao.messages(
                    conversationPartnerId = conversationPartnerId.value!!,
                    page = 1,
                    pageSize = 1,
                    accessToken = user.getBearerAccessToken()!!
                )
                val status = response.status
                val messagesResponse = response.data
                if (status.isSuccessful && messagesResponse != null) {
                    if (messagesResponse.data?.isEmpty() == false) {
                        val messageResponse = messagesResponse.data!![0]
                        val message = toMessage(messageResponse)
                        return Result(message, null)
                    } else {
                        return Result(null, null)
                    }
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