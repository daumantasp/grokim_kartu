package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.data.players.entities.FirestorePlayer
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val playersRepository: PlayersRepository
) : ViewModel() {
    private val _players = MutableLiveData<List<FirestorePlayer>>()
    var players: LiveData<List<FirestorePlayer>> = _players

    companion object {
        private val TAG = "PlayersViewModel"
    }

    fun loadPlayers() {
        playersRepository.getPlayers() { isSuccessful, firestorePlayers, e ->
            if (isSuccessful && firestorePlayers != null) {
                val list: MutableList<FirestorePlayer> = mutableListOf()
                list.addAll(firestorePlayers)
                _players.value = list
            }
        }
    }

    fun backClicked() {
        // TODO
    }
}