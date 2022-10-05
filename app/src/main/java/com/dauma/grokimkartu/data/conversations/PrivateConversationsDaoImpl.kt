package com.dauma.grokimkartu.data.conversations

import com.dauma.grokimkartu.data.conversations.entities.ConversationResponse
import com.dauma.grokimkartu.data.conversations.entities.MessageResponse
import com.dauma.grokimkartu.data.conversations.entities.MessagesResponse
import com.dauma.grokimkartu.data.conversations.entities.PostMessageRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class PrivateConversationsDaoImpl(retrofit: Retrofit) : PrivateConversationsDao {
    private val retrofitConversations: RetrofitConversations = retrofit.create(RetrofitConversations::class.java)

    override fun conversations(
        accessToken: String,
        onComplete: (List<ConversationResponse>?, ConversationsDaoResponseStatus) -> Unit
    ) {
        retrofitConversations.conversations(accessToken).enqueue(object : Callback<ArrayList<ConversationResponse>> {
            override fun onResponse(
                call: Call<ArrayList<ConversationResponse>>,
                response: Response<ArrayList<ConversationResponse>>
            ) {
                when (response.code()) {
                    200 -> {
                        val conversationListResponse = response.body()
                        val status = ConversationsDaoResponseStatus(true, null)
                        onComplete(conversationListResponse, status)
                    }
                    else -> {
                        val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<ConversationResponse>>, t: Throwable) {
                val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun messages(
        conversationPartnerId: Int,
        page: Int,
        pageSize: Int,
        accessToken: String,
        onComplete: (MessagesResponse?, ConversationsDaoResponseStatus) -> Unit
    ) {
        retrofitConversations.messages(conversationPartnerId, page, pageSize, accessToken).enqueue(object : Callback<MessagesResponse> {
            override fun onResponse(
                call: Call<MessagesResponse>,
                response: Response<MessagesResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val messagesResponse = response.body()
                        val status = ConversationsDaoResponseStatus(true, null)
                        onComplete(messagesResponse, status)
                    }
                    400 -> {
                        val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.INVALID_USER_ID)
                        onComplete(null, status)
                    }
                    500 -> {
                        val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.INTERNAL_SERVER_ERROR)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<MessagesResponse>, t: Throwable) {
                val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun postMessage(
        conversationPartnerId: Int,
        postMessageRequest: PostMessageRequest,
        accessToken: String,
        onComplete: (MessageResponse?, ConversationsDaoResponseStatus) -> Unit
    ) {
        retrofitConversations.postMessage(accessToken, conversationPartnerId, postMessageRequest).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                when (response.code()) {
                    201 -> {
                        val messageResponse = response.body()
                        val status = ConversationsDaoResponseStatus(true, null)
                        onComplete(messageResponse, status)
                    }
                    400 -> {
                        val status = ConversationsDaoResponseStatus(true, ConversationsDaoResponseStatus.Errors.INVALID_USER_ID)
                        onComplete(null, status)
                    }
                    500 -> {
                        val status = ConversationsDaoResponseStatus(true, ConversationsDaoResponseStatus.Errors.INTERNAL_SERVER_ERROR)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                val status = ConversationsDaoResponseStatus(false, ConversationsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    private interface RetrofitConversations {
        @GET ("conversations")
        fun conversations(@Header("Authorization") accessToken: String) : Call<ArrayList<ConversationResponse>>

        @GET("messages")
        fun messages(
            @Query("user_id") userId: Int,
            @Query("page") page: Int,
            @Query("page_size") pageSize: Int,
            @Header("Authorization") accessToken: String
        ): Call<MessagesResponse>

        @POST("messages")
        fun postMessage(
            @Header("Authorization") accessToken: String,
            @Query("user_id") user_id: Int,
            @Body postMessageRequest: PostMessageRequest
        ): Call<MessageResponse>
    }
}