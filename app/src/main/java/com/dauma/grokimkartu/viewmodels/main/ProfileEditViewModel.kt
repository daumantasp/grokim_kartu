package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.models.forms.ProfileEditForm
import com.dauma.grokimkartu.repositories.profile.ProfileRepository
import com.dauma.grokimkartu.repositories.profile.entities.UpdateProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val profileEditForm: ProfileEditForm
) : ViewModel() {
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _uiState = MutableLiveData<UiState>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val uiState: LiveData<UiState> = _uiState

    enum class UiState {
        FORM,
        BACK_CONFIRMATION,
        INSTRUMENT,
        CITY
    }

    init {
        loadProfile()
        loadPickableCities()
        loadPickableInstruments()
    }

    private fun loadProfile() {
        profileRepository.profile { profile, _ ->
            profileEditForm.setInitialValues(
                name = profile?.name,
                instrument = profile?.instrument,
                description = profile?.description,
                city = profile?.city
            )
        }
        profileRepository.photo { photo, _ ->
            profileEditForm.setInitialPhoto(photo)
        }
    }

    private fun loadPickableCities() {
        profileRepository.cities { citiesResponse, profileErrors ->
            if (citiesResponse != null) {
                profileEditForm.pickableCities = citiesResponse
                profileEditForm.filteredPickableCities = citiesResponse
            }
        }
    }

    private fun loadPickableInstruments() {
        profileRepository.instruments { instrumentsResponse, profileErrors ->
            if (instrumentsResponse != null) {
                profileEditForm.pickableInstruments = instrumentsResponse
                profileEditForm.filteredPickableInstruments = instrumentsResponse
            }
        }
    }

    fun getProfileEditForm(): ProfileEditForm {
        return profileEditForm
    }

    fun backClicked() {
        if (listOf(UiState.CITY, UiState.INSTRUMENT).contains(_uiState.value)) {
            _uiState.value = UiState.FORM
        } else if (profileEditForm.isChanged() && _uiState.value == UiState.FORM) {
            _uiState.value = UiState.BACK_CONFIRMATION
        } else {
            _navigation.value = Event(NavigationCommand.Back)
        }
    }

    fun cancelBackClicked() {
        _uiState.value = UiState.FORM
    }

    fun cancelPickerClicked() {
        _uiState.value = UiState.FORM
    }

    fun cityClicked() {
        profileEditForm.filteredPickableCities = profileEditForm.pickableCities
        _uiState.value = UiState.CITY
    }

    fun citySelected(id: Int) {
        val city = profileEditForm.pickableCities.firstOrNull { pc -> pc.id == id }
        city?.let {
            profileEditForm.city = it
            _uiState.value = UiState.FORM
        }
    }

    fun searchCity(value: String, onComplete: () -> Unit) {
        if (value.length > 2) {
            profileRepository.searchCity(value) { citiesResponse, profileErrors ->
                if (citiesResponse != null) {
                    profileEditForm.filteredPickableCities = citiesResponse
                }
                onComplete()
            }
        } else {
            profileEditForm.filteredPickableCities = profileEditForm.pickableCities
            onComplete()
        }
    }

    fun instrumentClicked() {
        profileEditForm.filteredPickableInstruments = profileEditForm.pickableInstruments
        _uiState.value = UiState.INSTRUMENT
    }

    fun instrumentSelected(id: Int) {
        val instrument = profileEditForm.pickableInstruments.firstOrNull { pi -> pi.id == id }
        instrument?.let {
            profileEditForm.instrument = it
            _uiState.value = UiState.FORM
        }
    }

    fun searchInstrument(value: String, onComplete: () -> Unit) {
        if (value.length > 2) {
            profileRepository.searchInstrument(value) { instrumentsResponse, profileErrors ->
                if (instrumentsResponse != null) {
                    profileEditForm.filteredPickableInstruments = instrumentsResponse
                }
                onComplete()
            }
        } else {
            profileEditForm.filteredPickableInstruments = profileEditForm.pickableInstruments
            onComplete()
        }
    }

    fun saveChanges(onComplete: () -> Unit = {}) {
        if (profileEditForm.areValuesChanged()) {
            val updatedProfile = UpdateProfile(
                description = profileEditForm.description,
                cityId = profileEditForm.city.id,
                instrumentId = profileEditForm.instrument.id
            )

            profileRepository.update(updatedProfile) { profile, profileErrors ->
                this.profileEditForm.setInitialValues(
                    name = profile?.name,
                    instrument = profile?.instrument,
                    description = profile?.description,
                    city = profile?.city
                )
            }
        }

        if (profileEditForm.isPhotoChanged()) {
            if (profileEditForm.photo != null) {
                profileRepository.updatePhoto(profileEditForm.photo!!) { updatedPhoto, profileErrors ->
                    if (updatedPhoto != null) {
                        this.profileEditForm.setInitialPhoto(updatedPhoto)
                    }
                    onComplete()
                }
            } else {
                onComplete()
            }
        } else {
            onComplete()
        }
    }
}