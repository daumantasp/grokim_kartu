package com.dauma.grokimkartu.data.auth.entities

import com.google.gson.annotations.SerializedName

data class RegistrationRequest (
    @SerializedName("name") var name: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("password") var password: String?,
    @SerializedName("password_confirmation") var passwordConfirmation: String?
)