package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.players.entities.Player
import com.dauma.grokimkartu.general.event.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val playersRepository: PlayersRepository
) : ViewModel() {
    private val _players = MutableLiveData<List<Player>>()
    private val _playerDetails = MutableLiveData<Event<String>>()
    var players: LiveData<List<Player>> = _players
    var playerDetails: LiveData<Event<String>> = _playerDetails

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

    fun playerClicked(userId: String) {
        _playerDetails.value = Event(userId)
    }
}