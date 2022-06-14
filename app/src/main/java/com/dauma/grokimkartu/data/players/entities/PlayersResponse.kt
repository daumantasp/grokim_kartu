package com.dauma.grokimkartu.data.players.entities

import com.dauma.grokimkartu.data.entities.PageDataResponse
import com.google.gson.annotations.SerializedName

data class PlayersResponse(
    @SerializedName("data") var data: ArrayList<PlayerResponse>?,
    @SerializedName("page_data") var pageData: PageDataResponse?
)