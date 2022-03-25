package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDate
import com.dauma.grokimkartu.models.forms.ThomannEditForm
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ThomannEditViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository,
    private val thomannEditForm: ThomannEditForm,
    private val utils: Utils
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _validUntil = MutableLiveData<Event<List<CustomDate>>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val validUndtil: LiveData<Event<List<CustomDate>>> = _validUntil

    fun thomannEditForm(): ThomannEditForm {
        return thomannEditForm
    }

    fun viewIsReady() {

    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun validUntilClicked() {
        val currentDate = utils.timeUtils.getCurrentDate()
        val minDate = utils.timeUtils.addDays(currentDate, 1)
        val maxDate = utils.timeUtils.addYears(currentDate, 1)
        _validUntil.value = Event(listOf(currentDate, minDate, maxDate))
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
            id = null,
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
            thomannsRepository.saveThomann(thomann) { isSuccessful, e ->
                if (isSuccessful) {
                    _navigateBack.value = Event("")
                }
            }
        } catch (e: Exception) {

        }
    }
}