package com.dauma.grokimkartu.data.instruments.entities

import com.google.gson.annotations.SerializedName

data class InstrumentResponse (
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?
)