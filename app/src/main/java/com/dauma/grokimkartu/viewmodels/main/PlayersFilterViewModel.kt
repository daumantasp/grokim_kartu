package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.models.forms.PlayersFilterForm
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayersFilterViewModel @Inject constructor(
    private val playersRepository: PlayersRepository,
    private val playersFilterForm: PlayersFilterForm
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _city = MutableLiveData<Event<String>>()
    private val _instrument = MutableLiveData<Event<String>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val city: LiveData<Event<String>> = _city
    val instrument: LiveData<Event<String>> = _instrument

    companion object {
        private val TAG = "PlayersFilterViewModel"
    }

    fun getPlayersFilterForm() : PlayersFilterForm {
        return playersFilterForm
    }

    fun viewIsReady() {
        loadPickableCities()
        loadPickableInstruments()
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun cityClicked() {
        playersFilterForm.filteredPickableCities = playersFilterForm.pickableCities
        _city.value = Event("")
    }

    fun instrumentClicked() {
        playersFilterForm.filteredPickableInstruments = playersFilterForm.pickableInstruments
        _instrument.value = Event("")
    }

    fun searchCity(value: String, onComplete: () -> Unit) {
        if (value.length > 2) {
            playersRepository.searchCity(value) { citiesResponse, profileErrors ->
                if (citiesResponse != null) {
                    playersFilterForm.filteredPickableCities = citiesResponse
                }
                onComplete()
            }
        } else {
            playersFilterForm.filteredPickableCities = playersFilterForm.pickableCities
            onComplete()
        }
    }

    fun searchInstrument(value: String, onComplete: () -> Unit) {
        if (value.length > 2) {
            playersRepository.searchInstrument(value) { instrumentsResponse, profileErrors ->
                if (instrumentsResponse != null) {
                    playersFilterForm.filteredPickableInstruments = instrumentsResponse
                }
                onComplete()
            }
        } else {
            playersFilterForm.filteredPickableInstruments = playersFilterForm.pickableInstruments
            onComplete()
        }
    }

    private fun loadPickableCities() {
        playersRepository.cities { citiesResponse, profileErrors ->
            if (citiesResponse != null) {
                playersFilterForm.pickableCities = citiesResponse
                playersFilterForm.filteredPickableCities = citiesResponse
            }
        }
    }

    private fun loadPickableInstruments() {
        playersRepository.instruments { instrumentsResponse, profileErrors ->
            if (instrumentsResponse != null) {
                playersFilterForm.pickableInstruments = instrumentsResponse
                playersFilterForm.filteredPickableInstruments = instrumentsResponse
            }
        }
    }
}