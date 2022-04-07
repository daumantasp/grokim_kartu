package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.models.forms.ProfileEditForm
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val profileEditForm: ProfileEditForm,
    private val utils: Utils
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack

    fun profileEditForm(): ProfileEditForm {
        return profileEditForm
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }
}