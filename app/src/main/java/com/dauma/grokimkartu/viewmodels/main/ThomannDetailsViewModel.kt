package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.utils.time.TimeUtils
import com.dauma.grokimkartu.models.forms.ThomannDetailsForm
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ThomannDetailsViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository,
    private val usersRepository: UsersRepository,
    private val thomannDetailsForm: ThomannDetailsForm,
    private val timeUtils: TimeUtils,
    savedStateHandle: SavedStateHandle
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

    fun joinClicked() {

    }

    fun loadDetails() {
        thomannsRepository.getThomann(thomannId ?: "") { thomann, thomannsError ->
            if (thomann != null) {
                val currentTime = Date()
                val creationDate = this.timeUtils.format(thomann.creationDate?.toDate() ?: currentTime)
                val validUntilDate = this.timeUtils.format(thomann.validUntil?.toDate() ?: currentTime)
                this.thomannDetailsForm.setInitialValues(
                    thomannId ?: "",
                    thomann.name ?: "",
                    thomann.city ?: "",
                    thomann.isLocked ?: false,
                    creationDate,
                    validUntilDate,
                    this.thomannsRepository.isJoinPossible(thomann)
                )
            }
        }
    }
}