package com.dauma.grokimkartu.viewmodels.main

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.repositories.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val name: String? = null,
    val userIcon: Bitmap? = null,
    val unreadCount: Int? = null,
    val isNotificationsStarted: Boolean = false,
    val isPlayersStarted: Boolean = false,
    val isThomannStarted: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val TAG = "HomeViewModel"
    }

    init {
        viewModelScope.launch {
            observeUnreadNotificationsCount()
        }
        viewModelScope.launch {
            setUserIcon()
        }
        viewModelScope.launch {
            setUserProfile()
        }
    }

    fun notifications() = _uiState.update { it.copy(isNotificationsStarted = true) }

    fun players() = _uiState.update { it.copy(isPlayersStarted = true) }

    fun thomann() = _uiState.update { it.copy(isThomannStarted = true) }

    fun notificationsStarted() = _uiState.update { it.copy(isNotificationsStarted = false) }

    fun playersStarted() = _uiState.update { it.copy(isPlayersStarted = false) }

    fun thomannStarted() = _uiState.update { it.copy(isThomannStarted = false) }

    private suspend fun setUserIcon() {
        val result = profileRepository.icon()
        _uiState.update { it.copy(userIcon = result.data) }
    }

    private suspend fun setUserProfile() {
        val result = profileRepository.profile()
        _uiState.update { it.copy(name = result.data?.name) }
    }

    private suspend fun observeUnreadNotificationsCount() {
        profileRepository.unreadCount.collect { profileUnreadCount ->
            _uiState.update { it.copy(unreadCount = profileUnreadCount?.unreadNotificationsCount) }
        }
    }
}