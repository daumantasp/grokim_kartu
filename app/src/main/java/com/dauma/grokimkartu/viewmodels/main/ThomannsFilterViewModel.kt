package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTime
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.models.forms.ThomannsFilterForm
import com.dauma.grokimkartu.repositories.thomanns.ThomannsFilter
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ThomannsFilterUiState(
    val close: Boolean = false,
    val isCitySelectionStarted: Boolean = false,
    val isDateSelectionStarted: Boolean = false,
    val date: ThomannEditDateData? = null
)

@HiltViewModel
class ThomannsFilterViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository,
    private val thomannsFilterForm: ThomannsFilterForm,
    private val utils: Utils
) : ViewModel() {

    private val _uiState = MutableStateFlow(ThomannsFilterUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadCities()
            setFilter()
        }
    }

    fun getThomannsFilterForm() = thomannsFilterForm

    private suspend fun loadCities() {
        val citiesResponse = thomannsRepository.cities()
        citiesResponse.data?.let {
            thomannsFilterForm.pickableCities = it
            thomannsFilterForm.filteredPickableCities = it
        }
    }

    private fun setFilter() {
        val cityOrNull = thomannsFilterForm.pickableCities.firstOrNull { pc ->
            pc.id == thomannsRepository.paginator.filter.value.cityId
        }

        val validUntil: String? = thomannsRepository.paginator.filter.value.validUntil
        val showOnlyUnlocked = thomannsRepository.paginator.filter.value.isLocked == false

        thomannsFilterForm.setInitialValues(
            city = cityOrNull,
            validUntil = validUntil,
            showOnlyUnlocked = showOnlyUnlocked
        )
    }

    fun back() {
        if (uiState.value.isCitySelectionStarted || uiState.value.isDateSelectionStarted) {
            _uiState.update { it.copy(isCitySelectionStarted = false, isDateSelectionStarted = false) }
        } else {
            _uiState.update { it.copy(close = true) }
        }
    }

    fun cancelPickerClicked() {
        if (uiState.value.isCitySelectionStarted || uiState.value.isDateSelectionStarted) {
            _uiState.update { it.copy(isCitySelectionStarted = false, isDateSelectionStarted = false) }
        }
    }

    fun cityClicked() {
        thomannsFilterForm.filteredPickableCities = thomannsFilterForm.pickableCities
        _uiState.update { it.copy(isCitySelectionStarted = true) }
    }

    fun searchCity(value: String, onComplete: () -> Unit) {
        if (value.length > 2) {
            viewModelScope.launch {
                val searchResponse = thomannsRepository.searchCity(value)
                searchResponse.data?.let {
                    thomannsFilterForm.filteredPickableCities = it
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
            _uiState.update { it.copy(isCitySelectionStarted = false) }
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
        _uiState.update { it.copy(
            isDateSelectionStarted = true,
            date = ThomannEditDateData(selectedDate, minDate, maxDate, isSaveButtonEnabled))
        }
    }

    fun validUntilSelected(validUntilDateTime: CustomDateTime) {
        val formattedDate = utils.timeUtils.format(validUntilDateTime, CustomDateTimeFormatPattern.yyyyMMdd)
        getThomannsFilterForm().validUntil = formattedDate
        _uiState.update { it.copy(isDateSelectionStarted = false) }
    }

    fun applyFilter() {
        viewModelScope.launch {
            if (thomannsFilterForm.isChanged()) {
                var formattedValidUntil: String? = null
                if (thomannsFilterForm.validUntil.isNotBlank()) {
                    val validUntilAsCustomDateTime = utils.timeUtils.parseToCustomDateTime(thomannsFilterForm.validUntil)
                    validUntilAsCustomDateTime?.let {
                        formattedValidUntil = utils.timeUtils.format(it, CustomDateTimeFormatPattern.yyyyMMdd)
                    }
                }
                thomannsRepository.paginator.setFilterAndReload(ThomannsFilter(
                    cityId = thomannsFilterForm.city.id,
                    validUntil = formattedValidUntil,
                    isLocked = if (thomannsFilterForm.showOnlyUnlocked) false else null
                ))
                _uiState.update { it.copy(close = true) }
            }
        }
    }

    fun clearFilter() {
        viewModelScope.launch {
            if (!thomannsFilterForm.isInitialEmpty()) {
                thomannsRepository.paginator.setFilterAndReload(ThomannsFilter.CLEAR)
                setFilter()
                _uiState.update { it.copy(close = true) }
            }
        }
    }
}