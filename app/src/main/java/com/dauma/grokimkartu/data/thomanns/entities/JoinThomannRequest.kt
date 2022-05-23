package com.dauma.grokimkartu.data.thomanns.entities

import com.google.gson.annotations.SerializedName

data class JoinThomannRequest(
    @SerializedName("amount") var amount: Double?
)
