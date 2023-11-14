package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.models.forms.ProfileForm
import com.dauma.grokimkartu.repositories.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isProfileEditStarted: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val profileForm: ProfileForm
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadProfile()
        }
        viewModelScope.launch {
            loadProfilePhoto()
        }
    }

    fun getProfileForm() : ProfileForm = profileForm

    fun editProfile() = _uiState.update { it.copy(isProfileEditStarted = true) }

    fun editProfileStarted() = _uiState.update { it.copy(isProfileEditStarted = false) }

    private suspend fun loadProfile() {
        val profile = profileRepository.profile()
        profileForm.setValues(
            name = profile.data?.name,
            instrument = profile.data?.instrument?.name,
            description = profile.data?.description,
            city = profile.data?.city?.name
        )
    }

    private suspend fun loadProfilePhoto() {
        val profilePhoto = profileRepository.photo()
        profileForm.photo = profilePhoto.data
    }
}