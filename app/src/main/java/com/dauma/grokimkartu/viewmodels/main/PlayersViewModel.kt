package com.dauma.grokimkartu.viewmodels.main

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.users.UsersRepository
import com.dauma.grokimkartu.repositories.users.entities.Profile
import com.dauma.grokimkartu.ui.main.adapters.PlayersListData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val playersRepository: PlayersRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {
    private val _userProfile = MutableLiveData<Profile>()
    private val _userIcon = MutableLiveData<Bitmap?>()
    private val _playersListData = MutableLiveData<List<PlayersListData>>()
    private val _playerDetails = MutableLiveData<Event<String>>()
    private val _navigateToProfile = MutableLiveData<Event<String>>()
    val userProfile: LiveData<Profile> = _userProfile
    val userIcon: LiveData<Bitmap?> = _userIcon
    val playersListData: LiveData<List<PlayersListData>> = _playersListData
    val playerDetails: LiveData<Event<String>> = _playerDetails
    val navigateToProfile: LiveData<Event<String>> = _navigateToProfile

    companion object {
        private val TAG = "PlayersViewModel"
    }

    fun viewIsReady() {
        loadUserProfile()
        loadUserIcon()
        loadPlayers()
    }

    fun backClicked() {
        // TODO
    }

    fun userIconClicked() {
        _navigateToProfile.value = Event("")
    }

    fun playerClicked(userId: String) {
        _playerDetails.value = Event(userId)
    }

    private fun loadUserProfile() {
        usersRepository.getUserProfile() { profile, e ->
            if (profile != null) {
                this._userProfile.value = profile
            }
        }
    }

    private fun loadUserIcon() {
        usersRepository.getUserIcon() { icon, e ->
            this._userIcon.value = icon
        }
    }

    private fun loadPlayers() {
        playersRepository.getPlayers() { isSuccessful, players, e ->
            if (isSuccessful && players != null) {
                val list: MutableList<PlayersListData> = mutableListOf()
                for (player in players) {
                    list.add(PlayersListData((player)))
                }
                _playersListData.value = list
            }
        }
    }
}