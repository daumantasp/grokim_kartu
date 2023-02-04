package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.utils.Utils
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
    private val _navigateBackConfirmation = MutableLiveData<Event<String>>()
    private val _city = MutableLiveData<Event<String>>()
    private val _validUntil = MutableLiveData<Event<List<Any>>>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val navigateBackConfirmation: LiveData<Event<String>> = _navigateBackConfirmation
    val city: LiveData<Event<String>> = _city
    val validUntil: LiveData<Event<List<Any>>> = _validUntil

    fun thomannEditForm(): ThomannEditForm {
        return thomannEditForm
    }

    fun viewIsReady() {
        loadDetailsIfNeeded()
        loadPickableCities()
    }

    private fun loadPickableCities() {
        thomannsRepository.cities { citiesResponse, thomannErrors ->
            if (citiesResponse != null) {
                thomannEditForm.pickableCities = citiesResponse
                thomannEditForm.filteredPickableCities = citiesResponse
            }
        }
    }

    fun searchCity(value: String, onComplete: () -> Unit) {
        if (value.length > 2) {
            thomannsRepository.searchCity(value) { citiesResponse, thomannErrors ->
                if (citiesResponse != null) {
                    thomannEditForm.filteredPickableCities = citiesResponse
                }
                onComplete()
            }
        } else {
            thomannEditForm.filteredPickableCities = thomannEditForm.pickableCities
            onComplete()
        }
    }

    fun backClicked(isConfirmed: Boolean) {
        if (thomannEditForm.isChanged() && isConfirmed == false) {
            _navigateBackConfirmation.value = Event("")
        } else {
            _navigation.value = Event(NavigationCommand.Back)
        }
    }

    fun loadDetailsIfNeeded() {
        if (thomannId != null) {
            thomannsRepository.thomannDetails(thomannId) { thomannDetails, thomannsErrors ->
                if (thomannDetails != null) {
                    var validUntilAsString = ""
                    if (thomannDetails.validUntil != null) {
                        validUntilAsString = utils.timeUtils.format(Date(thomannDetails.validUntil!!.time), CustomDateTimeFormatPattern.yyyyMMdd)
                    }
                    this.thomannEditForm.setInitialValues(
                        city = thomannDetails.city,
                        validUntil = validUntilAsString
                    )
                }
            }
        }
    }

    fun cityClicked() {
        thomannEditForm.filteredPickableCities = thomannEditForm.pickableCities
        _city.value = Event("")
    }

    fun citySelected(id: Int) {
        val city = thomannEditForm.pickableCities.firstOrNull { pc -> pc.id == id }
        if (city != null) {
            thomannEditForm.city = city
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
        _validUntil.value = Event(listOf(selectedDate, minDate, maxDate, isSaveButtonEnabled))
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
                thomannsRepository.create(createThomann) { thomannDetails, thomannsErrors ->
                    if (thomannDetails != null) {
                        _navigation.value = Event(NavigationCommand.Back)
                    }
                    onComplete()
                }
            } else {
                val updateThomann = UpdateThomann(
                    isLocked = null,
                    cityId = thomannEditForm.city.id,
                    validUntil = validUntilTimestamp
                )
                thomannsRepository.update(thomannId!!, updateThomann) { thomannDetails, thomannsErrors ->
                    if (thomannDetails != null) {
                        _navigation.value = Event(NavigationCommand.Back)
                    }
                    onComplete()
                }
            }
        } catch (e: Exception) {}
    }
}