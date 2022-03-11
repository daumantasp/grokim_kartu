package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.models.forms.ThomannDetailsForm
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ThomannDetailsViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository,
    private val thomannDetailsForm: ThomannDetailsForm,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val thomannId = savedStateHandle.get<String>("thomannId")
    private val _navigateBack = MutableLiveData<Event<String>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack

    companion object {
        private val TAG = "ThomannDetailsViewModel"
    }

    fun getThomannDetailsForm() : ThomannDetailsForm {
        return thomannDetailsForm
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun loadDetails() {
        thomannsRepository.getThomann(thomannId ?: "") { thomann, thomannsError ->
            val simpleDate = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            val creationDate = simpleDate.format(thomann?.creationDate?.toDate() ?: Date())
            val validUntilDate = simpleDate.format(thomann?.creationDate?.toDate() ?: Date())
            this.thomannDetailsForm.setInitialValues(
                thomannId ?: "",
                thomann?.name ?: "",
                thomann?.city ?: "",
                thomann?.isLocked ?: false,
                creationDate,
                validUntilDate
            )
        }
    }
}