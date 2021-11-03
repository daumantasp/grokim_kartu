package com.dauma.grokimkartu.data.players

import androidx.lifecycle.LiveData
import com.dauma.grokimkartu.data.players.entities.Player

interface PlayersDao {
    fun getPlayers() : LiveData<List<Player>>
    fun addPlayer(player: Player)
}