package com.dauma.grokimkartu.data.thomanns.entities

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class CreateThomannRequest(
    @SerializedName("city_id") var cityId: Int?,
    @SerializedName("valid_until") var validUntil: Timestamp?
)
