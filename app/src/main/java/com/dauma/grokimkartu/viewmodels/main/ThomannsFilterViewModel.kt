package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTime
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.models.forms.ThomannsFilterForm
import com.dauma.grokimkartu.repositories.thomanns.ThomannsFilter
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThomannsFilterViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository,
    private val thomannsFilterForm: ThomannsFilterForm,
    private val utils: Utils
) : ViewModel() {
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _uiState = MutableLiveData<UiState>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val uiState: LiveData<UiState> = _uiState

    sealed class UiState {
        object Form: UiState()
        object City: UiState()
        data class ValidUntil(
            val currentDate: CustomDateTime,
            val minDateTime: CustomDateTime,
            val maxDateTime: CustomDateTime,
            val isSaveButtonEnabled: Boolean
        ): UiState()
    }

    init {
        loadPickableValuesAndSetFilter()
    }

    private fun loadPickableValuesAndSetFilter() {
        loadPickableCities {
            setFilter()
        }
    }

    private fun loadPickableCities(onComplete: () -> Unit = {}) {
        thomannsRepository.cities { citiesResponse, profileErrors ->
            if (citiesResponse != null) {
                thomannsFilterForm.pickableCities = citiesResponse
                thomannsFilterForm.filteredPickableCities = citiesResponse
            }
            onComplete()
        }
    }

    private fun setFilter() {
        val cityOrNull = thomannsFilterForm.pickableCities.firstOrNull { pc ->
            pc.id == thomannsRepository.filter.cityId
        }
        val validUntil: String? = thomannsRepository.filter.validUntil
        val showOnlyUnlocked = thomannsRepository.filter.isLocked == false

        thomannsFilterForm.setInitialValues(
            city = cityOrNull,
            validUntil = validUntil,
            showOnlyUnlocked = showOnlyUnlocked
        )
    }

    fun getThomannsFilterForm() : ThomannsFilterForm {
        return thomannsFilterForm
    }

    fun backClicked() {
        if (_uiState.value is UiState.City || _uiState.value is UiState.ValidUntil) {
            _uiState.value = UiState.Form
        } else {
            _navigation.value = Event(NavigationCommand.Back)
        }
    }

    fun cancelPickerClicked() {
        _uiState.value = UiState.Form
    }

    fun cityClicked() {
        thomannsFilterForm.filteredPickableCities = thomannsFilterForm.pickableCities
        _uiState.value = UiState.City
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
        city?.let {
            thomannsFilterForm.city = it
            _uiState.value = UiState.Form
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
        _uiState.value = UiState.ValidUntil(selectedDate, minDate, maxDate, isSaveButtonEnabled)
    }

    fun validUntilSelected(validUntilDateTime: CustomDateTime) {
        val formattedDate = this.utils.timeUtils.format(validUntilDateTime, CustomDateTimeFormatPattern.yyyyMMdd)
        getThomannsFilterForm().validUntil = formattedDate
        _uiState.value = UiState.Form
    }

    fun applyFilter() {
        if (thomannsFilterForm.isChanged()) {
            var formattedValidUntil: String? = null
            if (thomannsFilterForm.validUntil.isNotBlank()) {
                val validUntilAsCustomDateTime = utils.timeUtils.parseToCustomDateTime(thomannsFilterForm.validUntil)
                validUntilAsCustomDateTime?.let {
                    formattedValidUntil = utils.timeUtils.format(it, CustomDateTimeFormatPattern.yyyyMMdd)
                }
            }
            thomannsRepository.filter = ThomannsFilter(
                cityId = thomannsFilterForm.city.id,
                validUntil = formattedValidUntil,
                isLocked = if (thomannsFilterForm.showOnlyUnlocked) false else null
            )
            _navigation.value = Event(NavigationCommand.Back)
        }
    }

    fun clearFilter() {
        if (thomannsFilterForm.isInitialEmpty() == false) {
            thomannsRepository.filter = ThomannsFilter.CLEAR
            setFilter()
            _navigation.value = Event(NavigationCommand.Back)
        }
    }
}