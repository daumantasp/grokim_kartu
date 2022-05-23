package com.dauma.grokimkartu.data.auth.entities

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class LoginUserResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("is_email_verified") var isEmailVerified: Boolean?,
    @SerializedName("created_at") var createdAt: Timestamp?,
    @SerializedName("photo_id") var photoId: String?
)