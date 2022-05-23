package com.dauma.grokimkartu.data.thomanns.entities

import com.google.gson.annotations.SerializedName

data class ThomannsDataResponse(
    @SerializedName("data") var data: ArrayList<ThomannResponse>?
)