package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannsPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AllThomannsUiState(
    val thomannsPages: List<ThomannsPage> = listOf(),
    val isThomannDetailsStarted: Int = -1
)

@HiltViewModel
class AllThomannsViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(AllThomannsUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val TAG = "AllThomannsViewModel"
    }

    init {
        viewModelScope.launch {
            observeThomannsPages()
        }
        loadNextThomannsPage()
    }

    fun thomannItemClicked(thomannId: Int) {
        thomannFindLoop@for (page in _uiState.value.thomannsPages) {
            page.thomanns?.let {  }
            if (page.thomanns != null) {
                for (thomann in page.thomanns) {
                    if (thomann.id == thomannId) {
                        if (thomann.isAccessible == true) {
                            _uiState.update { it.copy(isThomannDetailsStarted = thomannId ) }
                        }
                        break@thomannFindLoop
                    }
                }
            }
        }
    }

    fun loadNextThomannsPage() {
        viewModelScope.launch {
            thomannsRepository.paginator.loadNextPage()
        }
    }

    fun reload() {
        viewModelScope.launch {
            thomannsRepository.reload()
        }
    }

    private suspend fun observeThomannsPages() {
        thomannsRepository.paginator.pages.collect { thomannsPages ->
            _uiState.update { it.copy(thomannsPages = thomannsPages) }
        }
    }
}