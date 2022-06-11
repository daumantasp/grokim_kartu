package com.dauma.grokimkartu.data.thomanns.entities

import com.dauma.grokimkartu.data.entities.PageDataResponse
import com.google.gson.annotations.SerializedName

data class ThomannsResponse(
    @SerializedName("data") var data: ArrayList<ThomannResponse>?,
    @SerializedName("page_data") var pageData: PageDataResponse?
)