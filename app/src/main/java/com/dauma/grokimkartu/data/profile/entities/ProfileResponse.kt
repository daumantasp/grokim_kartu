package com.dauma.grokimkartu.data.profile.entities

import com.dauma.grokimkartu.data.cities.entities.CityResponse
import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class ProfileResponse(
    @SerializedName("id") var user: Int?,
    @SerializedName("name") var name: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("city") var city: CityResponse?,
    @SerializedName("instrument") var instrument: String?,
    @SerializedName("created_at") var createdAt: Timestamp?
)