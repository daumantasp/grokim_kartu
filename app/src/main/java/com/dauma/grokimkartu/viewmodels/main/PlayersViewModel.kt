package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.ui.main.adapters.PlayersListData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val playersRepository: PlayersRepository
) : ViewModel() {
    private val _playersListData = MutableLiveData<List<PlayersListData>>()
    private val _playerDetails = MutableLiveData<Event<Int>>()
    private val _navigateBack = MutableLiveData<Event<String>>()
    val playersListData: LiveData<List<PlayersListData>> = _playersListData
    val playerDetails: LiveData<Event<Int>> = _playerDetails
    val navigateBack: LiveData<Event<String>> = _navigateBack

    companion object {
        private val TAG = "PlayersViewModel"
    }

    fun viewIsReady() {
        loadPlayers()
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun playerClicked(userId: Int) {
        _playerDetails.value = Event(userId)
    }

    private fun loadPlayers() {
        playersRepository.players() { players, e ->
            if (players != null) {
                val list: MutableList<PlayersListData> = mutableListOf()
                for (player in players) {
                    list.add(PlayersListData((player)))
                }
                _playersListData.value = list
            }
        }
    }
}