package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.ui.main.adapters.ThomannsListData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThomannsViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository
): ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _navigateToCreation = MutableLiveData<Event<String>>()
    private val _thomannsListData = MutableLiveData<List<ThomannsListData>>()
    private val _thomannDetails = MutableLiveData<Event<Int>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val navigateToCreation: LiveData<Event<String>> = _navigateToCreation
    val thomannsListData: LiveData<List<ThomannsListData>> = _thomannsListData
    val thomannDetails: LiveData<Event<Int>> = _thomannDetails

    companion object {
        private val TAG = "ThomannViewModel"
    }

    fun viewIsReady() {
        loadThomanns()
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun thomannItemClicked(thomannId: Int) {
        for (thomannListData in thomannsListData.value ?: listOf()) {
            if (thomannListData.thomann.id == thomannId && thomannListData.thomann.isAccessible == true) {
                _thomannDetails.value = Event(thomannId)
                break
            }
        }
    }

    fun createClicked() {
        _navigateToCreation.value = Event("")
    }

    private fun loadThomanns() {
        thomannsRepository.thomanns() { thomanns, e ->
            if (thomanns != null) {
                val list: MutableList<ThomannsListData> = mutableListOf()
                for (thomann in thomanns) {
                    list.add(ThomannsListData(thomann))
                }
                _thomannsListData.value = list
            }
        }
    }
}