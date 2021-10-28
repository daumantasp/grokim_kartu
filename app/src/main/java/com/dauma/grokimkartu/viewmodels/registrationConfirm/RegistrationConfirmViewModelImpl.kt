package com.dauma.grokimkartu.viewmodels.registrationConfirm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.models.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationConfirmViewModelImpl @Inject constructor()
    : ViewModel(), RegistrationConfirmViewModel {
    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>> = _navigateToLogin

    companion object {
        private val TAG = "RegistrationConfirmViewModelImpl"
    }

    override fun next() {
        _navigateToLogin.value = Event(true)
    }
}