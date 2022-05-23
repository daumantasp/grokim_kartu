package com.dauma.grokimkartu.data.thomanns.entities

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class ThomannUserResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("user") var user: ThomannUserConciseResponse?,
    @SerializedName("amount") var amount: String?,
    @SerializedName("created_at") var createdAt: Timestamp?,
    @SerializedName("is_current_user") var isCurrentUser: Boolean?,
    @SerializedName("actions") var actions: ArrayList<String>?
)