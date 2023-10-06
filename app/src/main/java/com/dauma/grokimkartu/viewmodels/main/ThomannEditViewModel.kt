package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTime
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.models.forms.ThomannEditForm
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.repositories.thomanns.entities.CreateThomann
import com.dauma.grokimkartu.repositories.thomanns.entities.UpdateThomann
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.sql.Date
import java.sql.Timestamp
import javax.inject.Inject

@HiltViewModel
class ThomannEditViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository,
    private val thomannEditForm: ThomannEditForm,
    private val utils: Utils,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val thomannId = savedStateHandle.get<Int>("thomannId")
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _uiState = MutableLiveData<UiState>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val uiState: LiveData<UiState> = _uiState

    sealed class UiState {
        object Form: UiState()
        object BackConfirmation: UiState()
        object City: UiState()
        data class ValidUntil(
            val currentDate: CustomDateTime,
            val minDateTime: CustomDateTime,
            val maxDateTime: CustomDateTime,
            val isSaveButtonEnabled: Boolean
        ): UiState()
    }

    init {
        loadDetailsIfNeeded()
        loadPickableCities()
    }

    private fun loadDetailsIfNeeded() {
        thomannId?.let {
//            thomannsRepository.thomannDetails(it) { thomannDetails, thomannsErrors ->
//                if (thomannDetails != null) {
//                    var validUntilAsString = ""
//                    if (thomannDetails.validUntil != null) {
//                        validUntilAsString = utils.timeUtils.format(Date(thomannDetails.validUntil!!.time), CustomDateTimeFormatPattern.yyyyMMdd)
//                    }
//                    thomannEditForm.setInitialValues(
//                        city = thomannDetails.city,
//                        validUntil = validUntilAsString
//                    )
//                }
//            }
        }
    }

    private fun loadPickableCities() {
//        thomannsRepository.cities { citiesResponse, thomannErrors ->
//            citiesResponse?.let {
//                thomannEditForm.pickableCities = it
//                thomannEditForm.filteredPickableCities = it
//            }
//        }
    }

    fun thomannEditForm(): ThomannEditForm {
        return thomannEditForm
    }

    fun backClicked() {
        if (_uiState.value is UiState.City || _uiState.value is UiState.ValidUntil) {
            _uiState.value = UiState.Form
        } else if (thomannEditForm.isChanged() && _uiState.value is UiState.Form) {
            _uiState.value = UiState.BackConfirmation
        } else {
            _navigation.value = Event(NavigationCommand.Back)
        }
    }

    fun cancelBackClicked() {
        _uiState.value = UiState.Form
    }

    fun cancelPickerClicked() {
        _uiState.value = UiState.Form
    }

    fun cityClicked() {
        thomannEditForm.filteredPickableCities = thomannEditForm.pickableCities
        _uiState.value = UiState.City
    }

    fun searchCity(value: String, onComplete: () -> Unit) {
        if (value.length > 2) {
//            thomannsRepository.searchCity(value) { citiesResponse, thomannErrors ->
//                citiesResponse?.let {
//                    thomannEditForm.filteredPickableCities = it
//                }
//                onComplete()
//            }
        } else {
            thomannEditForm.filteredPickableCities = thomannEditForm.pickableCities
            onComplete()
        }
    }

    fun citySelected(id: Int) {
        val city = thomannEditForm.pickableCities.firstOrNull { pc -> pc.id == id }
        city?.let {
            thomannEditForm.city = it
            _uiState.value = UiState.Form
        }
    }

    fun validUntilClicked() {
        val currentDate = utils.timeUtils.getCurrentDateTime()
        val minDate = utils.timeUtils.addDays(currentDate, 1)
        val maxDate = utils.timeUtils.addYears(currentDate, 1)
        var selectedDate = currentDate
        var isSaveButtonEnabled = true
        if (thomannEditForm.validUntil != "") {
            val validUntilAsCustomDate = utils.timeUtils.parseToCustomDateTime(thomannEditForm.validUntil)
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
        thomannEditForm().validUntil = formattedDate
        _uiState.value = UiState.Form
    }

    fun saveChanges(onComplete: () -> Unit = {}) {
        val validUntilAsDate = utils.timeUtils.parseToCustomDateTime(thomannEditForm.validUntil)
        var validUntilTimestamp: Timestamp? = null
        if (validUntilAsDate != null) {
            val validUntilInMillis = utils.timeUtils.convertToTimeInMillis(validUntilAsDate)
            validUntilTimestamp = Timestamp(validUntilInMillis)
        }
        try {
            if (thomannId == null) {
                val createThomann = CreateThomann(
                    cityId = thomannEditForm.city.id,
                    validUntil = validUntilTimestamp
                )
//                thomannsRepository.create(createThomann) { thomannDetails, thomannsErrors ->
//                    if (thomannDetails != null) {
//                        _navigation.value = Event(NavigationCommand.Back)
//                    }
//                    onComplete()
//                }
            } else {
                val updateThomann = UpdateThomann(
                    isLocked = null,
                    cityId = thomannEditForm.city.id,
                    validUntil = validUntilTimestamp
                )
//                thomannsRepository.update(thomannId!!, updateThomann) { thomannDetails, thomannsErrors ->
//                    if (thomannDetails != null) {
//                        _navigation.value = Event(NavigationCommand.Back)
//                    }
//                    onComplete()
//                }
            }
        } catch (e: Exception) {}
    }
}