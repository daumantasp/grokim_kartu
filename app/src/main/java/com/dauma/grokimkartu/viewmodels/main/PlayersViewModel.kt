package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.players.entities.PlayersPage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val playersRepository: PlayersRepository
) : ViewModel() {
    private val _playersPages = MutableLiveData<List<PlayersPage>>()
    private val _playerDetails = MutableLiveData<Event<Int>>()
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _filter = MutableLiveData<Event<String>>()
    private val _filterEnabled = MutableLiveData<Event<Boolean>>()
    val playersPages: LiveData<List<PlayersPage>> = _playersPages
    val playerDetails: LiveData<Event<Int>> = _playerDetails
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val filter: LiveData<Event<String>> = _filter
    val filterEnabled: LiveData<Event<Boolean>> = _filterEnabled

    companion object {
        private val TAG = "PlayersViewModel"
    }

    fun viewIsReady() {
        if (playersRepository.pages.isEmpty()) {
            loadNextPlayersPage()
        } else {
            _playersPages.value = playersRepository.pages
        }
        _filterEnabled.value = Event(playersRepository.isFilterApplied)
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun filterClicked() {
        _filter.value = Event("")
    }

    fun playerClicked(userId: Int) {
        _playerDetails.value = Event(userId)
    }

    fun loadNextPlayersPage() {
        playersRepository.loadNextPage() { _, _ ->
            _playersPages.value = playersRepository.pages
        }
    }

    fun reload() {
        playersRepository.reload { _, _ ->
            _playersPages.value = playersRepository.pages
        }
    }
}