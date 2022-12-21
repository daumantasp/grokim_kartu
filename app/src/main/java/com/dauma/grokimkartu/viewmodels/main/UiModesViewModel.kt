package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.utils.sharedstorage.SharedStorageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UiModesViewModel @Inject constructor(
    private val sharedStorageUtils: SharedStorageUtils
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack

    companion object {
        private val TAG = "UiModesViewModel"
    }

    fun viewIsReady() {
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }
}