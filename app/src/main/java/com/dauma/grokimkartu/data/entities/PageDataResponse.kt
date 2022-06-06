package com.dauma.grokimkartu.data.entities

import com.google.gson.annotations.SerializedName

data class PageDataResponse(
    @SerializedName("current_page") var currentPage: Int?,
    @SerializedName("last_page") var lastPage: Int?,
    @SerializedName("page_size") var pageSize: Int?,
    @SerializedName("total_items") var totalItems: Int?
)
