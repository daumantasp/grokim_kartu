package com.dauma.grokimkartu.data.cities.entities

import com.google.gson.annotations.SerializedName

data class CityResponse (
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?
)