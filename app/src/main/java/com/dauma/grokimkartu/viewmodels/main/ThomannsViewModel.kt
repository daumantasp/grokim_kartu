package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThomannsViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository
): ViewModel() {
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _allThomannsDisplayed = MutableLiveData<Event<Boolean>>()
    private val _filter = MutableLiveData<Event<String>>()
    private val _filterEnabled = MutableLiveData<Event<Boolean>>()
    private var isAllThomannsTabSelected: Boolean = true
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val allThomannsDisplayed: LiveData<Event<Boolean>> = _allThomannsDisplayed
    val filter: LiveData<Event<String>> = _filter
    val filterEnabled: LiveData<Event<Boolean>> = _filterEnabled

    companion object {
        private val TAG = "ThomannsViewModel"
    }

    fun viewIsReady() {
        _filterEnabled.value = Event(thomannsRepository.isFilterApplied)
    }

    fun backClicked() {
        _navigation.value = Event(NavigationCommand.Back)
    }

    fun tabSelected(isAllThomannsTabSelected: Boolean) {
        this.isAllThomannsTabSelected = isAllThomannsTabSelected
        _allThomannsDisplayed.value = Event(isAllThomannsTabSelected)
    }

    fun filterClicked() {
        if (isAllThomannsTabSelected) {
            _filter.value = Event("")
        }
    }
}