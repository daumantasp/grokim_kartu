package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.models.forms.ThomannEditForm
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ThomannEditViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository,
    private val thomannEditForm: ThomannEditForm,
    private val utils: Utils,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val thomannId = savedStateHandle.get<String>("thomannId")
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _validUntil = MutableLiveData<Event<List<Any>>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val validUntil: LiveData<Event<List<Any>>> = _validUntil

    fun thomannEditForm(): ThomannEditForm {
        return thomannEditForm
    }

    fun viewIsReady() {
        loadDetailsIfNeeded()
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun loadDetailsIfNeeded() {
        if (thomannId != null) {
            thomannsRepository.getThomann(thomannId) { thomann, thomannsError ->
                var validUntilAsString = ""
                if (thomann?.validUntil != null) {
                    validUntilAsString = utils.timeUtils.format(thomann.validUntil!!.toDate())
                }
                this.thomannEditForm.setInitialValues(
                    city = thomann?.city ?: "",
                    validUntil = validUntilAsString
                )
            }
        }
    }

    fun validUntilClicked() {
        val currentDate = utils.timeUtils.getCurrentDate()
        val minDate = utils.timeUtils.addDays(currentDate, 1)
        val maxDate = utils.timeUtils.addYears(currentDate, 1)
        var selectedDate = currentDate
        var isSaveButtonEnabled = true
        if (thomannEditForm.validUntil != "") {
            val validUntilAsCustomDate = utils.timeUtils.parseToDate(thomannEditForm.validUntil)
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

    fun saveClicked(city: String, validUntil: String) {
        val validUntilAsDate = utils.timeUtils.parseToDate(validUntil)
        var validUntilTimestamp: Timestamp? = null
        if (validUntilAsDate != null) {
            val validUntilInMillis = utils.timeUtils.convertToTimeInMillis(validUntilAsDate)
            val validUntilInSeconds = validUntilInMillis / 1000L
            validUntilTimestamp = Timestamp(validUntilInSeconds, 0)
        }
        val thomann = Thomann(
            id = thomannId,
            userId = null,
            name = null,
            city = city,
            isLocked = null,
            creationDate = null,
            validUntil = validUntilTimestamp,
            users = null,
            icon = null
        )
        try {
            if (thomannId == null) {
                thomannsRepository.saveThomann(thomann) { isSuccessful, e ->
                    if (isSuccessful) {
                        _navigateBack.value = Event("")
                    }
                }
            } else {
                thomannsRepository.updateThomann(thomann) { isSuccessful, e ->
                    if (isSuccessful) {
                        _navigateBack.value = Event("")
                    }
                }
            }
        } catch (e: Exception) {}
    }
}