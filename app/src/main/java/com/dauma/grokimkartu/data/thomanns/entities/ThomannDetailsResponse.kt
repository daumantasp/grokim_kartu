package com.dauma.grokimkartu.data.thomanns.entities

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class ThomannDetailsResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("user") var user: ThomannUserConciseResponse?,
    @SerializedName("city") var city: String?,
    @SerializedName("is_owner") var isOwner: Boolean?,
    @SerializedName("is_locked") var isLocked: Boolean?,
    @SerializedName("created_at") var createdAt: Timestamp?,
    @SerializedName("valid_until") var validUntil: Timestamp?,
    @SerializedName("users") var users: ArrayList<ThomannUserResponse>?,
    @SerializedName("totalAmount") var totalAmount: String?,
    @SerializedName("actions") var actions: ArrayList<String>?
)