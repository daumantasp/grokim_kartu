package com.dauma.grokimkartu.data.conversations

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.conversations.entities.ConversationResponse
import com.dauma.grokimkartu.data.conversations.entities.MessageResponse
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.data.conversations.entities.PostMessageRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class PrivateConversationsDaoImpl(retrofit: Retrofit) : PrivateConversationsDao {
    private val retrofitConversations: RetrofitConversations = retrofit.create(RetrofitConversations::class.java)

    override suspend fun conversations(accessToken: String): DaoResult<List<ConversationResponse>?, ConversationsDaoResponseStatus> {
        val response = retrofitConversations.conversations(accessToken)

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

    override suspend fun messages(
        conversationPartnerId: Int,
        page: Int,
        pageSize: Int,
        accessToken: String
    ): DaoResult<MessagesResponse?, ConversationsDaoResponseStatus> {
        val response = retrofitConversations.messages(conversationPartnerId, page, pageSize, accessToken)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val messagesResponse = response.body()
                    val status = ConversationsDaoResponseStatus(true, null)
                    return DaoResult(messagesResponse, status)
                }
                400 -> {
                    val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.INVALID_USER_ID)
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

    override suspend fun postMessage(
        conversationPartnerId: Int,
        postMessageRequest: PostMessageRequest,
        accessToken: String
    ): DaoResult<MessageResponse?, ConversationsDaoResponseStatus> {
        val response = retrofitConversations.postMessage(accessToken, conversationPartnerId, postMessageRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                201 -> {
                    val messageResponse = response.body()
                    val status = ConversationsDaoResponseStatus(true, null)
                    return DaoResult(messageResponse, status)
                }
                400 -> {
                    val status = ConversationsDaoResponseStatus(true, ConversationsDaoResponseStatus.Errors.INVALID_USER_ID)
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
        @GET ("conversations")
        suspend fun conversations(@Header("Authorization") accessToken: String) : Response<ArrayList<ConversationResponse>>

        @GET("messages")
        suspend fun messages(
            @Query("user_id") userId: Int,
            @Query("page") page: Int,
            @Query("page_size") pageSize: Int,
            @Header("Authorization") accessToken: String
        ): Response<MessagesResponse>

        @POST("messages")
        suspend fun postMessage(
            @Header("Authorization") accessToken: String,
            @Query("user_id") user_id: Int,
            @Body postMessageRequest: PostMessageRequest
        ): Response<MessageResponse>
    }
}