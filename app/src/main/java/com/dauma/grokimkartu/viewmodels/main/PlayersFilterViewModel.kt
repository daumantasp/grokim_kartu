package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.models.forms.PlayersFilterForm
import com.dauma.grokimkartu.repositories.players.PlayersFilter
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayersFilterViewModel @Inject constructor(
    private val playersRepository: PlayersRepository,
    private val playersFilterForm: PlayersFilterForm
) : ViewModel() {
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _uiState = MutableLiveData<UiState>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val uiState: LiveData<UiState> = _uiState

    enum class UiState {
        FORM,
        CITY,
        INSTRUMENT
    }

    init {
        loadPickableValuesAndSetFilter()
    }

    private fun loadPickableValuesAndSetFilter() {
        var isPickableCitiesLoaded = false
        var isPickableInstrumentsLoaded = false

        fun setFilterIfAllValuesLoaded() {
            if (isPickableCitiesLoaded && isPickableInstrumentsLoaded) {
                setFilter()
            }
        }

        loadPickableCities {
            isPickableCitiesLoaded = true
            setFilterIfAllValuesLoaded()
        }
        loadPickableInstruments {
            isPickableInstrumentsLoaded = true
            setFilterIfAllValuesLoaded()
        }
    }

    private fun setFilter() {
//        val cityOrNull = playersFilterForm.pickableCities.firstOrNull { pc ->
//            pc.id == playersRepository.filter.cityId
//        }
//        val instrumentOrNull = playersFilterForm.pickableInstruments.firstOrNull { pi ->
//            pi.id == playersRepository.filter.instrumentId
//        }
//        playersFilterForm.setInitialValues(
//            city = cityOrNull,
//            instrument = instrumentOrNull,
//            text = playersRepository.filter.text
//        )
    }

    private fun loadPickableCities(onComplete: () -> Unit = {}) {
//        playersRepository.cities { citiesResponse, profileErrors ->
//            if (citiesResponse != null) {
//                playersFilterForm.pickableCities = citiesResponse
//                playersFilterForm.filteredPickableCities = citiesResponse
//            }
//            onComplete()
//        }
    }

    private fun loadPickableInstruments(onComplete: () -> Unit = {}) {
//        playersRepository.instruments { instrumentsResponse, profileErrors ->
//            if (instrumentsResponse != null) {
//                playersFilterForm.pickableInstruments = instrumentsResponse
//                playersFilterForm.filteredPickableInstruments = instrumentsResponse
//            }
//            onComplete()
//        }
    }

    fun getPlayersFilterForm() : PlayersFilterForm {
        return playersFilterForm
    }

    fun backClicked() {
        if (listOf(UiState.CITY, UiState.INSTRUMENT).contains(_uiState.value)) {
            _uiState.value = UiState.FORM
        } else {
            _navigation.value = Event(NavigationCommand.Back)
        }
    }

    fun cancelPickerClicked() {
        _uiState.value = UiState.FORM
    }

    fun cityClicked() {
        playersFilterForm.filteredPickableCities = playersFilterForm.pickableCities
        _uiState.value = UiState.CITY
    }

    fun instrumentClicked() {
        playersFilterForm.filteredPickableInstruments = playersFilterForm.pickableInstruments
        _uiState.value = UiState.INSTRUMENT
    }

    fun searchCity(value: String, onComplete: () -> Unit) {
        if (value.length > 2) {
//            playersRepository.searchCity(value) { citiesResponse, profileErrors ->
//                if (citiesResponse != null) {
//                    playersFilterForm.filteredPickableCities = citiesResponse
//                }
//                onComplete()
//            }
        } else {
            playersFilterForm.filteredPickableCities = playersFilterForm.pickableCities
            onComplete()
        }
    }

    fun searchInstrument(value: String, onComplete: () -> Unit) {
        if (value.length > 2) {
//            playersRepository.searchInstrument(value) { instrumentsResponse, profileErrors ->
//                if (instrumentsResponse != null) {
//                    playersFilterForm.filteredPickableInstruments = instrumentsResponse
//                }
//                onComplete()
//            }
        } else {
            playersFilterForm.filteredPickableInstruments = playersFilterForm.pickableInstruments
            onComplete()
        }
    }

    fun citySelected(id: Int) {
        val city = playersFilterForm.pickableCities.firstOrNull { pc -> pc.id == id }
        city?.let {
            playersFilterForm.city = it
            _uiState.value = UiState.FORM
        }
    }

    fun instrumentSelected(id: Int) {
        val instrument = playersFilterForm.pickableInstruments.firstOrNull { pi -> pi.id == id }
        instrument?.let {
            playersFilterForm.instrument = it
            _uiState.value = UiState.FORM
        }
    }

    fun applyFilter() {
        if (playersFilterForm.isChanged()) {
//            playersRepository.filter = PlayersFilter(
//                cityId = playersFilterForm.city.id,
//                instrumentId = playersFilterForm.instrument.id,
//                text = playersFilterForm.text
//            )
            _navigation.value = Event(NavigationCommand.Back)
        }
    }

    fun clearFilter() {
        if (playersFilterForm.isInitialEmpty() == false) {
//            playersRepository.filter = PlayersFilter.CLEAR
//            setFilter()
//            _navigation.value = Event(NavigationCommand.Back)
        }
    }
}