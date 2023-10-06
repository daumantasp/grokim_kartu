package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.players.entities.PlayersPage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val playersRepository: PlayersRepository
) : ViewModel() {
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _playersPages = MutableLiveData<List<PlayersPage>>()
    private val _filterEnabled = MutableLiveData<Event<Boolean>>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val playersPages: LiveData<List<PlayersPage>> = _playersPages
    val filterEnabled: LiveData<Event<Boolean>> = _filterEnabled

    companion object {
        private val TAG = "PlayersViewModel"
    }

    fun viewIsReady() {
//        if (playersRepository.pages.isEmpty()) {
//            loadNextPlayersPage()
//        } else {
//            _playersPages.value = playersRepository.pages
//        }
//        _filterEnabled.value = Event(playersRepository.isFilterApplied)
    }

    fun backClicked() {
        _navigation.value = Event(NavigationCommand.Back)
    }

    fun loadNextPlayersPage() {
//        playersRepository.loadNextPage() { _, _ ->
//            _playersPages.value = playersRepository.pages
//        }
    }

    fun reload() {
//        playersRepository.reload { _, _ ->
//            _playersPages.value = playersRepository.pages
//        }
    }
}