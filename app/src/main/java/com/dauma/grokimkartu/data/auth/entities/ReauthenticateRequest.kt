package com.dauma.grokimkartu.data.auth.entities

import com.google.gson.annotations.SerializedName

data class ReauthenticateRequest(
    @SerializedName("token") var accessToken: String
)