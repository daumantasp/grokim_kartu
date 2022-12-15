package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThomannsFilterViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _city = MutableLiveData<Event<String>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val city: LiveData<Event<String>> = _city

    companion object {
        private val TAG = "ThomannsFilterViewModel"
    }

//    fun getPlayersFilterForm() : PlayersFilterForm {
//        return playersFilterForm
//    }

    fun viewIsReady() {
//        loadPickableValuesAndSetFilter()
    }

//    private fun loadPickableValuesAndSetFilter() {
//        var isPickableCitiesLoaded = false
//
//        fun setFilterIfAllValuesLoaded() {
//            if (isPickableCitiesLoaded && isPickableInstrumentsLoaded) {
//                setFilter()
//            }
//        }
//
//        loadPickableCities {
//            isPickableCitiesLoaded = true
//            setFilterIfAllValuesLoaded()
//        }
//    }

//    private fun setFilter() {
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
//    }

//    private fun loadPickableCities(onComplete: () -> Unit = {}) {
//        playersRepository.cities { citiesResponse, profileErrors ->
//            if (citiesResponse != null) {
//                playersFilterForm.pickableCities = citiesResponse
//                playersFilterForm.filteredPickableCities = citiesResponse
//            }
//            onComplete()
//        }
//    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

//    fun cityClicked() {
//        playersFilterForm.filteredPickableCities = playersFilterForm.pickableCities
//        _city.value = Event("")
//    }

//    fun searchCity(value: String, onComplete: () -> Unit) {
//        if (value.length > 2) {
//            playersRepository.searchCity(value) { citiesResponse, profileErrors ->
//                if (citiesResponse != null) {
//                    playersFilterForm.filteredPickableCities = citiesResponse
//                }
//                onComplete()
//            }
//        } else {
//            playersFilterForm.filteredPickableCities = playersFilterForm.pickableCities
//            onComplete()
//        }
//    }

//    fun citySelected(id: Int) {
//        val city = playersFilterForm.pickableCities.firstOrNull { pc -> pc.id == id }
//        if (city != null) {
//            playersFilterForm.city = city
//        }
//
//    }

//    fun applyFilter() {
//        if (playersFilterForm.isChanged()) {
//            playersRepository.filter = PlayersFilter(
//                cityId = playersFilterForm.city.id,
//                instrumentId = playersFilterForm.instrument.id,
//                text = playersFilterForm.text
//            )
//            _navigateBack.value = Event("")
//        }
//    }

//    fun clearFilter() {
//        if (playersFilterForm.isInitialEmpty() == false) {
//            playersRepository.filter = PlayersFilter.CLEAR
//            setFilter()
//            _navigateBack.value = Event("")
//        }
//    }
}