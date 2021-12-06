package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.data.players.entities.PlayerDao
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.players.entities.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val playersRepository: PlayersRepository
) : ViewModel() {
    private val _players = MutableLiveData<List<Player>>()
    var players: LiveData<List<Player>> = _players

    companion object {
        private val TAG = "PlayersViewModel"
    }

    fun loadPlayers() {
        playersRepository.getPlayers() { isSuccessful, players, e ->
            if (isSuccessful && players != null) {
                val list: MutableList<Player> = mutableListOf()
                list.addAll(players)
                _players.value = list
            }
        }
    }

    fun backClicked() {
        // TODO
    }
}