package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.models.forms.ThomannEditForm
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ThomannEditViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository,
    private val thomannEditForm: ThomannEditForm
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack

    fun thomannEditForm(): ThomannEditForm {
        return thomannEditForm
    }

    fun viewIsReady() {

    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun saveClicked(city: String, validUntil: String) {
        val thomann = Thomann(null, null, city, null, null, null)
        try {
            thomannsRepository.saveThomann(thomann) { isSuccessful, e ->

            }
        } catch (e: Exception) {

        }
    }
}