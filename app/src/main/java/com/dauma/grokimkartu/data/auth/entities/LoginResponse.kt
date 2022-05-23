package com.dauma.grokimkartu.data.auth.entities

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("user") var user: LoginUserResponse?,
    @SerializedName("token") var accessToken: String?
)