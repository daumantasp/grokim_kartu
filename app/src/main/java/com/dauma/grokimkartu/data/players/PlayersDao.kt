package com.dauma.grokimkartu.data.players

import com.dauma.grokimkartu.data.players.entities.PlayerDao

interface PlayersDao {
    fun getPlayers(onComplete: (Boolean, List<PlayerDao>?, Exception?) -> Unit)
//    fun getPlayerDetails(userId: String, onComplete: (Boolean, PlayerDetails?, Exception?) -> Unit)
}