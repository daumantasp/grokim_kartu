package com.dauma.grokimkartu.data.players

import com.dauma.grokimkartu.data.players.entities.FirestorePlayer

interface PlayersDao {
    fun getPlayers(onComplete: (Boolean, List<FirestorePlayer>?, Exception?) -> Unit)
    fun getPlayer(userId: String, onComplete: (Boolean, FirestorePlayer?, Exception?) -> Unit)
    fun setPlayer(player: FirestorePlayer, onComplete: (Boolean, Exception?) -> Unit)
    fun deletePlayer(userId: String, onComplete: (Boolean, Exception?) -> Unit)
}