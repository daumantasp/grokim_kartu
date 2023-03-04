package com.dauma.grokimkartu.data.conversations

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.conversations.entities.ConversationResponse
import com.dauma.grokimkartu.data.conversations.entities.MessageResponse
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.data.conversations.entities.PostMessageRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class ThomannConversationsDaoImpl(retrofit: Retrofit) : ThomannConversationsDao {
    private val retrofitConversations: RetrofitConversations = retrofit.create(RetrofitConversations::class.java)

    override suspend fun thomannConversations(accessToken: String): DaoResult<List<ConversationResponse>?, ConversationsDaoResponseStatus> {
        val response = retrofitConversations.thomannConversations(accessToken)
        
        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val conversationListResponse = response.body()
                    val status = ConversationsDaoResponseStatus(true, null)
                    return DaoResult(conversationListResponse, status)
                }
                else -> {
                    val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun thomannMessages(
        thomannId: Int,
        page: Int,
        pageSize: Int,
        accessToken: String
    ): DaoResult<MessagesResponse?, ConversationsDaoResponseStatus> {
        val response = retrofitConversations.thomannMessages(thomannId, page, pageSize, accessToken)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val messagesResponse = response.body()
                    val status = ConversationsDaoResponseStatus(true, null)
                    return DaoResult(messagesResponse, status)
                }
                404 -> {
                    val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                    return DaoResult(null, status)
                }
                403 -> {
                    val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.FORBIDDEN)
                    return DaoResult(null, status)
                }
                500 -> {
                    val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.INTERNAL_SERVER_ERROR)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun postThomannMessage(
        thomannId: Int,
        postMessageRequest: PostMessageRequest,
        accessToken: String
    ): DaoResult<MessageResponse?, ConversationsDaoResponseStatus> {
        val response = retrofitConversations.postThomannMessage(accessToken, thomannId, postMessageRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                201 -> {
                    val messageResponse = response.body()
                    val status = ConversationsDaoResponseStatus(true, null)
                    return DaoResult(messageResponse, status)
                }
                404 -> {
                    val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                    return DaoResult(null, status)
                }
                403 -> {
                    val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.FORBIDDEN)
                    return DaoResult(null, status)
                }
                500 -> {
                    val status = ConversationsDaoResponseStatus(true, ConversationsDaoResponseStatus.Errors.INTERNAL_SERVER_ERROR)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    private interface RetrofitConversations {
        @GET ("conversations/thomann")
        suspend fun thomannConversations(@Header("Authorization") accessToken: String) : Response<ArrayList<ConversationResponse>>

        @GET("messages/thomann")
        suspend fun thomannMessages(
            @Query("thomann_id") thomannId: Int,
            @Query("page") page: Int,
            @Query("page_size") pageSize: Int,
            @Header("Authorization") accessToken: String
        ): Response<MessagesResponse>

        @POST("messages/thomann")
        suspend fun postThomannMessage(
            @Header("Authorization") accessToken: String,
            @Query("thomann_id") thomann_id: Int,
            @Body postMessageRequest: PostMessageRequest
        ): Response<MessageResponse>
    }
}