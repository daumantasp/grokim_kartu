package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.models.forms.ProfileEditForm
import com.dauma.grokimkartu.repositories.profile.ProfileRepository
import com.dauma.grokimkartu.repositories.profile.entities.UpdateProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileEditUiState(
    val close: Boolean = false,
    val isCitySelectionStarted: Boolean = false,
    val isInstrumentSelectionStarted: Boolean = false,
    val isConfirmation: Boolean = false
)

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val profileEditForm: ProfileEditForm
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch { loadProfile() }
            launch { loadPhoto() }
        }
        viewModelScope.launch {
            val cities = async { loadCities() }
            val instruments = async { loadInstruments() }
            cities.await()
            instruments.await()
        }
    }

    fun getProfileEditForm() : ProfileEditForm = profileEditForm

    fun back() {
        if (uiState.value.isCitySelectionStarted || uiState.value.isInstrumentSelectionStarted) {
            _uiState.update { it.copy(isCitySelectionStarted = false, isInstrumentSelectionStarted = false) }
        } else if (profileEditForm.isChanged() && !_uiState.value.isConfirmation) {
            _uiState.update { it.copy(isConfirmation = true) }
        } else {
            _uiState.update { it.copy(isConfirmation = false, close = true) }
        }
    }

    fun cancelBack() {
        _uiState.update { it.copy(isConfirmation = false) }
    }

    fun cityClicked() {
        profileEditForm.filteredPickableCities = profileEditForm.pickableCities
        _uiState.update { it.copy(isCitySelectionStarted = true) }
    }

    fun instrumentClicked() {
        profileEditForm.filteredPickableInstruments = profileEditForm.pickableInstruments
        _uiState.update { it.copy(isInstrumentSelectionStarted = true) }
    }

    fun cancelPickerClicked() {
        if (uiState.value.isCitySelectionStarted || uiState.value.isInstrumentSelectionStarted) {
            _uiState.update { it.copy(isCitySelectionStarted = false, isInstrumentSelectionStarted = false) }
        }
    }

    fun searchCity(value: String, onComplete: () -> Unit) {
        if (value.length > 2) {
            viewModelScope.launch {
                val searchResponse = profileRepository.searchCity(value)
                searchResponse.data?.let {
                    profileEditForm.filteredPickableCities = it
                }
                onComplete()
            }
        } else {
            profileEditForm.filteredPickableCities = profileEditForm.pickableCities
            onComplete()
        }
    }

    fun searchInstrument(value: String, onComplete: () -> Unit) {
        if (value.length > 2) {
            viewModelScope.launch {
                val searchResponse = profileRepository.searchInstrument(value)
                searchResponse.data?.let {
                    profileEditForm.filteredPickableInstruments = it
                }
                onComplete()
            }
        } else {
            profileEditForm.filteredPickableInstruments = profileEditForm.pickableInstruments
            onComplete()
        }
    }

    fun citySelected(id: Int) {
        val city = profileEditForm.pickableCities.firstOrNull { pc -> pc.id == id }
        city?.let {
            profileEditForm.city = it
            _uiState.update { it.copy(isCitySelectionStarted = false) }
        }
    }

    fun instrumentSelected(id: Int) {
        val instrument = profileEditForm.pickableInstruments.firstOrNull { pi -> pi.id == id }
        instrument?.let {
            profileEditForm.instrument = it
            _uiState.update { it.copy(isInstrumentSelectionStarted = false) }
        }
    }

    fun saveChanges(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val saveProfileTask = async { saveProfile() }
            val savePhotoTask = async { savePhoto() }
            saveProfileTask.await()
            savePhotoTask.await()
            onComplete()
        }
    }

    private suspend fun saveProfile() {
        if (!profileEditForm.areValuesChanged())
            return

        val updatedProfile = UpdateProfile(
            description = profileEditForm.description,
            cityId = profileEditForm.city.id,
            instrumentId = profileEditForm.instrument.id
        )

        viewModelScope.launch {
            val updateResponse = profileRepository.update(updatedProfile)
            updateResponse.data?.let {
                profileEditForm.setInitialValues(
                    name = it.name,
                    instrument = it.instrument,
                    description = it.description,
                    city = it.city
                )
            }
        }
    }

    private suspend fun savePhoto() {
        if (!profileEditForm.isPhotoChanged())
            return

        profileEditForm.photo?.let {
            viewModelScope.launch {
                val updateResponse = profileRepository.updatePhoto(it)
                updateResponse.data?.let {
                    profileEditForm.setInitialPhoto(it)
                }
            }
        }
    }

    private suspend fun loadProfile() {
        val profileResponse = profileRepository.profile()
        profileResponse.data?.let {
            profileEditForm.setInitialValues(
                name = it.name,
                instrument = it.instrument,
                description = it.description,
                city = it.city
            )
        }
    }

    private suspend fun loadPhoto() {
        val photoResponse = profileRepository.photo()
        photoResponse.data?.let {
            profileEditForm.setInitialPhoto(it)
        }
    }

    private suspend fun loadCities() {
        val citiesResponse = profileRepository.cities()
        citiesResponse.data?.let {
            profileEditForm.pickableCities = it
            profileEditForm.filteredPickableCities = it
        }
    }

    private suspend fun loadInstruments() {
        val instrumentsResponse = profileRepository.instruments()
        instrumentsResponse.data?.let {
            profileEditForm.pickableInstruments = it
            profileEditForm.filteredPickableInstruments = it
        }
    }
}