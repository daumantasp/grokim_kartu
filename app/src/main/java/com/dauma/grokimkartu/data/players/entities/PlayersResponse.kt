package com.dauma.grokimkartu.data.players.entities

import com.google.gson.annotations.SerializedName

data class PlayersResponse(
    @SerializedName("data") var data: ArrayList<PlayerResponse>?
)