package com.dauma.grokimkartu.data.conversations.entities

import com.dauma.grokimkartu.data.entities.PageDataResponse
import com.google.gson.annotations.SerializedName

data class MessagesResponse(
    @SerializedName("data") var data: ArrayList<MessageResponse>?,
    @SerializedName("page_data") var pageData: PageDataResponse?
)