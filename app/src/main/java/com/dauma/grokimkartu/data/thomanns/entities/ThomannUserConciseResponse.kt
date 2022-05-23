package com.dauma.grokimkartu.data.thomanns.entities

import com.google.gson.annotations.SerializedName

data class ThomannUserConciseResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?
)