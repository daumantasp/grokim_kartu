package com.dauma.grokimkartu.repositories.players

import com.dauma.grokimkartu.data.players.entities.FirestorePlayer

interface PlayersRepository {
    fun getPlayers(onComplete: (Boolean, List<FirestorePlayer>?, PlayersError?) -> Unit)
    fun getPlayer(userId: String, onComplete: (Boolean, FirestorePlayer?, PlayersError?) -> Unit)
    fun createPlayer(player: FirestorePlayer, onComplete: (Boolean, PlayersError?) -> Unit)
    fun updatePlayer(player: FirestorePlayer, onComplete: (Boolean, PlayersError?) -> Unit)
    fun deletePlayer(userId: String, onComplete: (Boolean, PlayersError?) -> Unit)
}