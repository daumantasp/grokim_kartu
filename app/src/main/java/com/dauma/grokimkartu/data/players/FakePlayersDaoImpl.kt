package com.dauma.grokimkartu.data.players

import androidx.lifecycle.MutableLiveData
import com.dauma.grokimkartu.data.players.entities.FirestorePlayer

class FakePlayersDaoImpl : PlayersDao {
    private val playerList = mutableListOf<FirestorePlayer>()
    private val players = MutableLiveData<List<FirestorePlayer>>()

    init {
        playerList.addAll(listOf(
            FirestorePlayer("1", true, "Daumantas", "Gitara"),
            FirestorePlayer("2", true, "Petras", "Armonika")
        ))
        players.value = playerList
    }

    override fun getPlayers(onComplete: (Boolean, List<FirestorePlayer>?, Exception?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getPlayer(
        userId: String,
        onComplete: (Boolean, FirestorePlayer?, Exception?) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override fun setPlayer(player: FirestorePlayer, onComplete: (Boolean, Exception?) -> Unit) {
        playerList.add(player)
        players.value = playerList
    }
    override fun deletePlayer(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        TODO("Not yet implemented")
    }
}