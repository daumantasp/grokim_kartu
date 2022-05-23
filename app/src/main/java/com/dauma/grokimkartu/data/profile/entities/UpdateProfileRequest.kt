package com.dauma.grokimkartu.data.profile.entities

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("description") var description: String?,
//    @SerializedName("is_visible") var isVisible: Boolean?,
    @SerializedName("city_id") var cityId: Int?,
    @SerializedName("instrument_id") var instrumentId: Int?
)