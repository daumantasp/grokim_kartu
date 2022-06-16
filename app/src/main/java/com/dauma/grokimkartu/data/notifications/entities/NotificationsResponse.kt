package com.dauma.grokimkartu.data.notifications.entities

import com.dauma.grokimkartu.data.entities.PageDataResponse
import com.google.gson.annotations.SerializedName

data class NotificationsResponse(
    @SerializedName("data") var data: ArrayList<NotificationResponse>?,
    @SerializedName("page_data") var pageData: PageDataResponse?
)