package com.dauma.grokimkartu.data.players

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dauma.grokimkartu.data.players.entities.Player

class FakePlayersDaoImpl : PlayersDao {
    private val playerList = mutableListOf<Player>()
    private val players = MutableLiveData<List<Player>>()

    init {
        playerList.addAll(listOf(
            Player(1, "Daumantas", "Gitara"),
            Player(2, "Petras", "Armonika")
        ))
        players.value = playerList
    }

    override fun addPlayer(player: Player) {
        playerList.add(player)
        players.value = playerList
    }

    override fun getPlayers(): LiveData<List<Player>> {
        return players as LiveData<List<Player>>
    }
}