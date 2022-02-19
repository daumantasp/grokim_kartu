package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.models.forms.PlayerDetailsForm
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerDetailsViewModel @Inject constructor(
    private val playersRepository: PlayersRepository,
    private val playerDetailsForm: PlayerDetailsForm,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    // READ https://medium.com/@fabioCollini/android-data-binding-f9f9d3afc761
    private val userId = savedStateHandle.get<String>("userId")
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _detailsLoaded = MutableLiveData<Event<String>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val detailsLoaded: LiveData<Event<String>> = _detailsLoaded

    companion object {
        private val TAG = "DetailsViewModel"
    }

    fun getPlayerDetailsForm() : PlayerDetailsForm {
        return playerDetailsForm
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun loadDetails() {
        var isDetailsLoaded = false
        var isPhotoLoaded = false
        fun checkIfFullProfileLoaded() {
            if (isDetailsLoaded == true && isPhotoLoaded == true) {
                _detailsLoaded.value = Event("")
            }
        }

        playersRepository.getPlayerDetails(userId ?: "") { playerDetails, playersError ->
            this.playerDetailsForm.setInitialValues(
                userId ?: "",
                playerDetails?.name ?: "",
                playerDetails?.instrument ?: "",
                playerDetails?.description ?: "",
                playerDetails?.city ?: ""
            )
            isDetailsLoaded = true
            checkIfFullProfileLoaded()
        }
        playersRepository.getPlayerPhoto(userId ?: "") { playerPhoto, playerError ->
            if (playerPhoto != null) {
                this.playerDetailsForm.setInitialPhoto(playerPhoto)
            }
            isPhotoLoaded = true
            checkIfFullProfileLoaded()
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