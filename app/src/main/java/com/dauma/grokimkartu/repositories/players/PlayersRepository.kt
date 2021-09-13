package com.dauma.grokimkartu.repositories.players

import androidx.lifecycle.LiveData
import com.dauma.grokimkartu.models.Player

interface PlayersRepository {
    fun getPlayers() : LiveData<List<Player>>
    fun addPlayer(player: Player)
}