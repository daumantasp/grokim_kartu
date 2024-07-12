package com.dauma.grokimkartu.repositories.conversations.paginator

import android.util.Log
import com.dauma.grokimkartu.data.conversations.ThomannConversationsDao
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

class ThomannConversationsPaginatorImpl(
    private val thomannConversationsDao: ThomannConversationsDao,
    playersDao: PlayersDao,
    private val user: User,
    private val utils: Utils
    ) : ConversationsPaginator(playersDao, user), ThomannConversationsPaginator {
    private var _thomannId: MutableStateFlow<Int?> = MutableStateFlow(null)
    override val thomannId: StateFlow<Int?> = _thomannId.asStateFlow()

    companion object {
        private const val CONVERSATION_PERIODIC_RELOAD = "CONVERSATION_PERIODIC_RELOAD"
    }

    override suspend fun loadNextPage(): Result<ConversationPage?, ConversationsErrors?> {
        if (_thomannId.value != null) {
            if (isLastLoaded() == false) {
                val nextPage = _pages.value.count() + 1
                val response = thomannConversationsDao.thomannMessages(_thomannId.value!!, nextPage, pageSize, user.getBearerAccessToken()!!)
                val status = response.status
                val messagesResponse = response.data
                if (status.isSuccessful && messagesResponse != null) {
                    val conversationPage = toConversationPage(messagesResponse)
                    val pages = _pages.value.toMutableList()
                    pages.add(conversationPage)
                    _pages.value = pages
                    return Result(conversationPage, null)
                } else {
                    return Result(null, ConversationsErrors.UNKNOWN)
                }
            } else {
                return Result(_pages.value.lastOrNull(), null)
            }
        } else {
            throw ConversationsException(ConversationsErrors.THOMANN_ID_NOT_SET)
        }
    }

    override fun setThomannId(id: Int?) {
        clear()
        _thomannId.value = id
        utils.dispatcherUtils.main.cancelPeriodic(CONVERSATION_PERIODIC_RELOAD)
        reloadConversationPeriodically()
    }

    private fun reloadConversationPeriodically() {
        // TODO: REFACTOR USING COROUTINES
        // FIX: KEEPS RUNNING WHEN VIEW IS NOT PRESENTED
        utils.dispatcherUtils.main.periodic(
            operationKey = CONVERSATION_PERIODIC_RELOAD,
            period = 1.0,
            startImmediately = true,
            repeats = true
        ) {
            Log.d("ThomannConversationsRepositoryImpl", "reloadConversationPeriodically")
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val isNeeded = isConversationReloadNeeded()
                        if (isNeeded) {
                            reload()
                        }
                    } catch (e: ConversationsException) {}
                }
        }
    }

    private suspend fun isConversationReloadNeeded(): Boolean {
        if (user.isUserLoggedIn()) {
            if (_thomannId.value != null) {
                if (isReloadInProgress == false) {
                    val response = loadLastMessage()
                    val message = response.data
                    val previousLastMessage = _pages.value.firstOrNull()?.messages?.firstOrNull()
                    return message?.id != previousLastMessage?.id
                } else {
                    return false
                }
            } else {
                throw ConversationsException(ConversationsErrors.THOMANN_ID_NOT_SET)
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private suspend fun reload(): Result<ConversationPage?, ConversationsErrors?> {
        if (user.isUserLoggedIn()) {
            if (thomannId.value != null) {
                if (isReloadInProgress == false) {
                    isReloadInProgress = true
                    clear()
                    val response = loadNextPage()
                    isReloadInProgress = false
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
                throw ConversationsException(ConversationsErrors.THOMANN_ID_NOT_SET)
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private suspend fun loadLastMessage(): Result<Message?, ConversationsErrors?> {
        if (user.isUserLoggedIn()) {
            if (_thomannId.value != null) {
                val response = thomannConversationsDao.thomannMessages(
                    thomannId = thomannId.value!!,
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
                throw ConversationsException(ConversationsErrors.THOMANN_ID_NOT_SET)
            }
        } else {
            throw ConversationsException(ConversationsErrors.USER_NOT_LOGGED_IN)
        }
    }
}