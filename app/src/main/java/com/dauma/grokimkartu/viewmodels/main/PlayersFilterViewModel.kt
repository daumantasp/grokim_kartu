package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.models.forms.PlayersFilterForm
import com.dauma.grokimkartu.repositories.players.PlayersFilter
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayersFilterUiState(
    val close: Boolean = false,
    val isCitySelectionStarted: Boolean = false,
    val isInstrumentSelectionStarted: Boolean = false
)

@HiltViewModel
class PlayersFilterViewModel @Inject constructor(
    private val playersRepository: PlayersRepository,
    private val playersFilterForm: PlayersFilterForm
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayersFilterUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val cities = async { loadCities() }
            val instruments = async { loadInstruments() }
            cities.await()
            instruments.await()
            setFilter()
        }
    }

    fun getPlayersFilterForm() : PlayersFilterForm = playersFilterForm

    fun back() {
        if (uiState.value.isCitySelectionStarted || uiState.value.isInstrumentSelectionStarted) {
            _uiState.update { it.copy(isCitySelectionStarted = false, isInstrumentSelectionStarted = false) }
        } else {
            _uiState.update { it.copy(close = true) }
        }
    }

    fun cityClicked() {
        playersFilterForm.filteredPickableCities = playersFilterForm.pickableCities
        _uiState.update { it.copy(isCitySelectionStarted = true) }
    }

    fun instrumentClicked() {
        playersFilterForm.filteredPickableInstruments = playersFilterForm.pickableInstruments
        _uiState.update { it.copy(isInstrumentSelectionStarted = true) }
    }

    fun cancelPickerClicked() {
        if (uiState.value.isCitySelectionStarted || uiState.value.isInstrumentSelectionStarted) {
            _uiState.update { it.copy(isCitySelectionStarted = false, isInstrumentSelectionStarted = false) }
        }
    }

    fun searchCity(value: String, onComplete: () -> Unit) {
        if (value.length > 2) {
            viewModelScope.launch {
                val searchResponse = playersRepository.searchCity(value)
                searchResponse.data?.let {
                    playersFilterForm.filteredPickableCities = it
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
            viewModelScope.launch {
                val searchResponse = playersRepository.searchInstrument(value)
                searchResponse.data?.let {
                    playersFilterForm.filteredPickableInstruments = it
                }
                onComplete()
            }
        } else {
            playersFilterForm.filteredPickableInstruments = playersFilterForm.pickableInstruments
            onComplete()
        }
    }

    fun citySelected(id: Int) {
        val city = playersFilterForm.pickableCities.firstOrNull { pc -> pc.id == id }
        city?.let {
            playersFilterForm.city = it
            _uiState.update { it.copy(isCitySelectionStarted = false) }
        }
    }

    fun instrumentSelected(id: Int) {
        val instrument = playersFilterForm.pickableInstruments.firstOrNull { pi -> pi.id == id }
        instrument?.let {
            playersFilterForm.instrument = it
            _uiState.update { it.copy(isInstrumentSelectionStarted = false) }
        }
    }

    fun applyFilter() {
        viewModelScope.launch {
            if (playersFilterForm.isChanged()) {
                playersRepository.paginator.setFilterAndReload(PlayersFilter(
                    cityId = playersFilterForm.city.id,
                    instrumentId = playersFilterForm.instrument.id,
                    text = playersFilterForm.text)
                )
                _uiState.update { it.copy(close = true) }
            }
        }
    }

    fun clearFilter() {
        viewModelScope.launch {
            if (!playersFilterForm.isInitialEmpty()) {
                playersRepository.paginator.setFilterAndReload(PlayersFilter.CLEAR)
                setFilter()
                _uiState.update { it.copy(close = true) }
            }
        }
    }

    private suspend fun loadCities() {
        val citiesResponse = playersRepository.cities()
        citiesResponse.data?.let {
            playersFilterForm.pickableCities = it
            playersFilterForm.filteredPickableCities = it
        }
    }

    private suspend fun loadInstruments() {
        val instrumentsResponse = playersRepository.instruments()
        instrumentsResponse.data?.let {
            playersFilterForm.pickableInstruments = it
            playersFilterForm.filteredPickableInstruments = it
        }
    }

    private fun setFilter() {
        val cityOrNull = playersFilterForm.pickableCities.firstOrNull { pc ->
            pc.id == playersRepository.paginator.filter.value.cityId
        }
        val instrumentOrNull = playersFilterForm.pickableInstruments.firstOrNull { pi ->
            pi.id == playersRepository.paginator.filter.value.instrumentId
        }
        playersFilterForm.setInitialValues(
            city = cityOrNull,
            instrument = instrumentOrNull,
            text = playersRepository.paginator.filter.value.text
        )
    }
}