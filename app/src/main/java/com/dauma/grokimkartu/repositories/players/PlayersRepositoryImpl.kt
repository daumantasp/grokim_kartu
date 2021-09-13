package com.dauma.grokimkartu.repositories.players

import androidx.lifecycle.LiveData
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.models.Player

class PlayersRepositoryImpl(private val playersDao: PlayersDao)
    : PlayersRepository {

    override fun addPlayer(player: Player) {
        playersDao.addPlayer(player)
    }

    override fun getPlayers() : LiveData<List<Player>> {
        return playersDao.getPlayers()
    }
}