package com.dauma.grokimkartu.data.players.entities

import android.graphics.Bitmap

data class PlayerDao(
    val userId: String?,
    val name: String?,
    val instrument: String?,
    val description: String?,
    val photo: Bitmap?,
    val city: String?
)