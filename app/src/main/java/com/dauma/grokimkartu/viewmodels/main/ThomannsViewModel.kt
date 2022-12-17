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
    private val _filter = MutableLiveData<Event<String>>()
    private val _filterEnabled = MutableLiveData<Event<Boolean>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val navigateToCreation: LiveData<Event<String>> = _navigateToCreation
    val filter: LiveData<Event<String>> = _filter
    val filterEnabled: LiveData<Event<Boolean>> = _filterEnabled

    companion object {
        private val TAG = "ThomannsViewModel"
    }

    fun viewIsReady() {
        _filterEnabled.value = Event(thomannsRepository.isFilterApplied)
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun filterClicked() {
        _filter.value = Event("")
    }

    fun createClicked() {
        _navigateToCreation.value = Event("")
    }
}