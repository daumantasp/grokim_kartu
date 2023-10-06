package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.ui.main.ThomannsFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThomannsViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository
): ViewModel() {
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _uiState = MutableLiveData<UiState>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val uiState: LiveData<UiState> = _uiState

    sealed class UiState {
        data class AllThomanns(val isFilterEnabled: Boolean): UiState()
        object MyThomanns: UiState()
    }

    init {
//        _uiState.value = UiState.AllThomanns(thomannsRepository.isFilterApplied)
    }

    fun backClicked() {
        _navigation.value = Event(NavigationCommand.Back)
    }

    fun tabSelected(isAllThomannsTabSelected: Boolean) {
//        _uiState.value = if (isAllThomannsTabSelected) {
//            UiState.AllThomanns(thomannsRepository.isFilterApplied)
//        } else {
//            UiState.MyThomanns
//        }
    }

    fun filterClicked() {
        if (_uiState.value is UiState.AllThomanns) {
            _navigation.value = Event(NavigationCommand.ToDirection(ThomannsFragmentDirections.actionThomannFragmentToThomannsFilterFragment()))
        }
    }
}