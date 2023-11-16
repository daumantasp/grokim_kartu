package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.models.forms.PlayerDetailsForm
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// READ https://medium.com/@fabioCollini/android-data-binding-f9f9d3afc761

data class PlayerDetailsUiState(
    val messageStarted: Boolean = false,
    val messageTitle: String? = null,
    val messageUserId: Int? = null,
    val close: Boolean = false
)

@HiltViewModel
class PlayerDetailsViewModel @Inject constructor(
    private val playersRepository: PlayersRepository,
    private val playerDetailsForm: PlayerDetailsForm,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val userId = savedStateHandle.get<Int>("userId")
    private val _uiState = MutableStateFlow(PlayerDetailsUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val TAG = "DetailsViewModel"
    }

    init {
        viewModelScope.launch {
            loadProfile()
        }
        viewModelScope.launch {
            loadProfilePhoto()
        }
    }

    fun getPlayerDetailsForm() : PlayerDetailsForm = playerDetailsForm

    fun back() = _uiState.update { it.copy(close = true) }

    fun report() {
        // TODO: not implemented
    }

    fun message() = _uiState.update {
        it.copy(messageStarted = true, messageTitle = playerDetailsForm.name, messageUserId = userId)
    }

    fun messageStarted() = _uiState.update {
        it.copy(messageStarted = false, messageTitle = null, messageUserId = null)
    }

    private suspend fun loadProfile() {
        userId?.let {
            val profile = playersRepository.playerDetails(it)
            playerDetailsForm.setInitialValues(
                userId = it,
                name = profile.data?.name ?: "",
                instrument = profile.data?.instrument ?: "",
                description = profile.data?.description ?: "",
                city = profile.data?.city ?: ""
            )
        }
    }

    private suspend fun loadProfilePhoto() {
        userId?.let {userId ->
            val profilePhoto = playersRepository.playerPhoto(userId)
            playerDetailsForm.photo = profilePhoto.data
        }
    }
}

// READ MORE //https://stackoverflow.com/questions/61089505/how-to-pass-argument-to-detail-viewmodel-via-navargs
// https://stackoverflow.com/questions/62650736/android-viewmodelfactory-with-hilt
// https://medium.com/mobile-app-development-publication/injecting-viewmodel-with-dagger-hilt-54ca2e433865
// https://medium.com/mobile-app-development-publication/passing-intent-data-to-viewmodel-711d72db20ad
//class PlayerDetailsViewModelFactory constructor(private val userId: String)
//    : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(PlayerDetailsViewModel::class.java)) {
//            return PlayerDetailsViewModel(userId) as T
//        }
//        throw IllegalArgumentException("Unknown Player Details ViewModel class")
//    }
//}