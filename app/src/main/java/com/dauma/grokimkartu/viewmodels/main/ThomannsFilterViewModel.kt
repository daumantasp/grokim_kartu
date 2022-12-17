package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.models.forms.ThomannsFilterForm
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThomannsFilterViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository,
    private val thomannsFilterForm: ThomannsFilterForm,
    private val utils: Utils
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _city = MutableLiveData<Event<String>>()
    private val _validUntil = MutableLiveData<Event<List<Any>>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val city: LiveData<Event<String>> = _city
    val validUntil: LiveData<Event<List<Any>>> = _validUntil

    companion object {
        private val TAG = "ThomannsFilterViewModel"
    }

    fun getThomannsFilterForm() : ThomannsFilterForm {
        return thomannsFilterForm
    }

    fun viewIsReady() {
        loadPickableValuesAndSetFilter()
    }

    private fun loadPickableValuesAndSetFilter() {
        var isPickableCitiesLoaded = false

//        fun setFilterIfAllValuesLoaded() {
//            if (isPickableCitiesLoaded && isPickableInstrumentsLoaded) {
//                setFilter()
//            }
//        }

        loadPickableCities {
            isPickableCitiesLoaded = true
//            setFilterIfAllValuesLoaded()
        }
    }

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

    private fun loadPickableCities(onComplete: () -> Unit = {}) {
        thomannsRepository.cities { citiesResponse, profileErrors ->
            if (citiesResponse != null) {
                thomannsFilterForm.pickableCities = citiesResponse
                thomannsFilterForm.filteredPickableCities = citiesResponse
            }
            onComplete()
        }
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun cityClicked() {
        thomannsFilterForm.filteredPickableCities = thomannsFilterForm.pickableCities
        _city.value = Event("")
    }

    fun searchCity(value: String, onComplete: () -> Unit) {
        if (value.length > 2) {
            thomannsRepository.searchCity(value) { citiesResponse, profileErrors ->
                if (citiesResponse != null) {
                    thomannsFilterForm.filteredPickableCities = citiesResponse
                }
                onComplete()
            }
        } else {
            thomannsFilterForm.filteredPickableCities = thomannsFilterForm.pickableCities
            onComplete()
        }
    }

    fun citySelected(id: Int) {
        val city = thomannsFilterForm.pickableCities.firstOrNull { pc -> pc.id == id }
        if (city != null) {
            thomannsFilterForm.city = city
        }
    }

    fun validUntilClicked() {
        val currentDate = utils.timeUtils.getCurrentDateTime()
        val minDate = currentDate
        val maxDate = utils.timeUtils.addYears(currentDate, 1)
        var selectedDate = currentDate
        var isSaveButtonEnabled = true
        if (thomannsFilterForm.validUntil != "") {
            val validUntilAsCustomDate = utils.timeUtils.parseToCustomDateTime(thomannsFilterForm.validUntil)
            if (validUntilAsCustomDate != null) {
                val validUntilInMillis = utils.timeUtils.convertToTimeInMillis(validUntilAsCustomDate)
                val currentDateInMillis = utils.timeUtils.convertToTimeInMillis(currentDate)
                if (validUntilInMillis > currentDateInMillis) {
                    selectedDate = validUntilAsCustomDate
                    isSaveButtonEnabled = false
                }
            }
        }
        _validUntil.value = Event(listOf(selectedDate, minDate, maxDate, isSaveButtonEnabled))
    }

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