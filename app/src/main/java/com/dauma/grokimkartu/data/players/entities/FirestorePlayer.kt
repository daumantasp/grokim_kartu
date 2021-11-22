package com.dauma.grokimkartu.data.players.entities

data class FirestorePlayer(
    val userId: String,
    val visible: Boolean,
    val name: String,
    val instrument: String
) {
}