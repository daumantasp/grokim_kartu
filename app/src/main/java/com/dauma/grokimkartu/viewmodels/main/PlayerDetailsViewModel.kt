package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerDetailsViewModel @Inject constructor(
    private val playersRepository: PlayersRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val userId = savedStateHandle.get<String>("userId")

    companion object {
        private val TAG = "DetailsViewModel"
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