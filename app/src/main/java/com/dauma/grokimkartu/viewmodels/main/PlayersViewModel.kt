package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.ui.main.adapters.PlayerLastInPageData
import com.dauma.grokimkartu.ui.main.adapters.PlayersListData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val playersRepository: PlayersRepository
) : ViewModel() {
    private val _playersListData = MutableLiveData<List<Any>>()
    private val _playerDetails = MutableLiveData<Event<Int>>()
    private val _navigateBack = MutableLiveData<Event<String>>()
    val playersListData: LiveData<List<Any>> = _playersListData
    val playerDetails: LiveData<Event<Int>> = _playerDetails
    val navigateBack: LiveData<Event<String>> = _navigateBack

    private var players: MutableList<Any> = mutableListOf()

    companion object {
        private val TAG = "PlayersViewModel"
    }

    fun viewIsReady() {
        loadNextPlayersPage()
    }

    fun backClicked() {
        playersRepository.clear()
        _navigateBack.value = Event("")
    }

    fun playerClicked(userId: Int) {
        _playerDetails.value = Event(userId)
    }

    fun loadNextPlayersPage() {
        playersRepository.loadNextPage() { playersPage, e ->
            if (playersPage?.players != null) {
                if (this.players.lastOrNull() is PlayerLastInPageData) {
                    this.players.removeLast()
                }
                for (player in playersPage.players) {
                    this.players.add(PlayersListData((player)))
                }
                if (playersPage.isLast == false) {
                    this.players.add(PlayerLastInPageData())
                }
                _playersListData.value = this.players
            }
        }
    }
}