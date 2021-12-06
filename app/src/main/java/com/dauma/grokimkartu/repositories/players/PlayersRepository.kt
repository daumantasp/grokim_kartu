package com.dauma.grokimkartu.repositories.players

import com.dauma.grokimkartu.repositories.players.entities.Player

interface PlayersRepository {
    fun getPlayers(onComplete: (Boolean, List<Player>?, PlayersError?) -> Unit)
//    fun getPlayerDetails(playerId: String, onComplete: (Boolean, PlayerDetails, PlayersError?) -> Unit)
}