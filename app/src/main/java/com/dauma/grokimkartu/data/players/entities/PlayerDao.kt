package com.dauma.grokimkartu.data.players.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class PlayerDao(
    val userId: String?,
    val name: String?,
    val instrument: String?,
    val description: String?
)