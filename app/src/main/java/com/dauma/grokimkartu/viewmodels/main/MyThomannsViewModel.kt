package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.repositories.thomanns.MyThomannsRepository
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannsPage
import com.dauma.grokimkartu.ui.main.ThomannsFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyThomannsViewModel @Inject constructor(
    private val myThomannsRepository: MyThomannsRepository
): ViewModel() {
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _thomannsPages = MutableLiveData<List<ThomannsPage>>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val thomannsPages: LiveData<List<ThomannsPage>> = _thomannsPages

    companion object {
        private val TAG = "MyThomannsViewModel"
    }

    fun viewIsReady() {
        if (myThomannsRepository.pages.isEmpty()) {
            loadThomannsNextPage()
        } else {
            _thomannsPages.value = myThomannsRepository.pages
        }
    }

    fun thomannItemClicked(thomannId: Int) {
        thomannFindLoop@for (page in myThomannsRepository.pages) {
            if (page.thomanns != null) {
                for (thomann in page.thomanns) {
                    if (thomann.id == thomannId) {
                        if (thomann.isAccessible == true) {
                            _navigation.value = Event(NavigationCommand.ToDirection(
                                ThomannsFragmentDirections.actionThomannFragmentToThomannDetailsFragment(thomannId))
                            )
                        }
                        break@thomannFindLoop
                    }
                }
            }
        }
    }

    fun loadThomannsNextPage() {
        myThomannsRepository.loadNextPage() { _, _ ->
            _thomannsPages.value = myThomannsRepository.pages
        }
    }

    fun reload() {
        myThomannsRepository.reload { _, _ ->
            _thomannsPages.value = myThomannsRepository.pages
        }
    }
}