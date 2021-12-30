package com.dauma.grokimkartu.repositories.players.entities

import android.graphics.Bitmap

data class PlayerDetails (
    val userId: String?,
    val name: String?,
    val instrument: String?,
    val description: String?,
    val photo: Bitmap?
)