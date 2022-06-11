package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.ui.main.adapters.ThomannLastInPageData
import com.dauma.grokimkartu.ui.main.adapters.ThomannsListData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThomannsViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository
): ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _navigateToCreation = MutableLiveData<Event<String>>()
    private val _thomannsListData = MutableLiveData<List<Any>>()
    private val _thomannDetails = MutableLiveData<Event<Int>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val navigateToCreation: LiveData<Event<String>> = _navigateToCreation
    val thomannsListData: LiveData<List<Any>> = _thomannsListData
    val thomannDetails: LiveData<Event<Int>> = _thomannDetails

    private var thomanns: MutableList<Any> = mutableListOf()

    companion object {
        private val TAG = "ThomannViewModel"
    }

    fun viewIsReady() {
        loadThomannsNextPage()
    }

    fun backClicked() {
        thomannsRepository.clear()
        _navigateBack.value = Event("")
    }

    fun thomannItemClicked(thomannId: Int) {
        for (thomannListData in thomannsListData.value ?: listOf()) {
            if (thomannListData is ThomannsListData) {
                if (thomannListData.thomann.id == thomannId && thomannListData.thomann.isAccessible == true) {
                    _thomannDetails.value = Event(thomannId)
                    break
                }
            }
        }
    }

    fun createClicked() {
        _navigateToCreation.value = Event("")
    }

    fun loadThomannsNextPage() {
        thomannsRepository.loadNextPage { thomannsPage, e ->
            if (thomannsPage?.thomanns != null) {
                val newThomannsData: MutableList<Any> = mutableListOf()
                newThomannsData.addAll(thomanns)
                if (newThomannsData.lastOrNull() is ThomannLastInPageData) {
                    newThomannsData.removeLast()
                }
                for (thomann in thomannsPage.thomanns) {
                    newThomannsData.add(ThomannsListData(thomann))
                }
                if (thomannsPage.isLast == false) {
                    newThomannsData.add(ThomannLastInPageData())
                }

                thomanns = newThomannsData
                _thomannsListData.value = thomanns
            }
        }
    }
}