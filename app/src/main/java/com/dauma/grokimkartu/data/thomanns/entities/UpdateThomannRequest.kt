package com.dauma.grokimkartu.data.thomanns.entities

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class UpdateThomannRequest(
    @SerializedName("is_locked") var isLocked: Boolean?,
    @SerializedName("city_id") var cityId: Int?,
    @SerializedName("valid_until") var validUntil: Timestamp?
)
