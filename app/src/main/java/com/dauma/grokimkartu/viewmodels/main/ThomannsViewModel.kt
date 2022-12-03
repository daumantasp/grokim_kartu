package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThomannsViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository
): ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _navigateToCreation = MutableLiveData<Event<String>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val navigateToCreation: LiveData<Event<String>> = _navigateToCreation

    companion object {
        private val TAG = "ThomannViewModel"
    }

    fun viewIsReady() {}

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun createClicked() {
        _navigateToCreation.value = Event("")
    }
}