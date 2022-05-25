package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.models.forms.ProfileEditForm
import com.dauma.grokimkartu.repositories.profile.ProfileRepository
import com.dauma.grokimkartu.repositories.profile.entities.UpdateProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val profileEditForm: ProfileEditForm,
    private val utils: Utils
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _selectPhoto = MutableLiveData<Event<String>>()
    private val _profileLoaded = MutableLiveData<Event<String>>()
    private val _city = MutableLiveData<Event<String>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val selectPhoto: LiveData<Event<String>> = _selectPhoto
    val profileLoaded: LiveData<Event<String>> = _profileLoaded
    val city: LiveData<Event<String>> = _city

    fun getProfileEditForm(): ProfileEditForm {
        return profileEditForm
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun viewIsReady() {
        loadProfile()
        loadPickableCities()
    }

    fun loadProfile() {
        var isProfileLoaded = false
        var isPhotoLoaded = false
        fun checkIfFullProfileLoaded() {
            if (isProfileLoaded == true && isPhotoLoaded == true) {
                _profileLoaded.value = Event("")
            }
        }

        profileRepository.profile { profile, profileErrors ->
            this.profileEditForm.setInitialValues(
                name = profile?.name,
                instrument = profile?.instrument,
                description = profile?.description,
                city = profile?.city
            )
            isProfileLoaded = true
            checkIfFullProfileLoaded()
        }

        profileRepository.photo { photo, profileErrors ->
            if (photo != null) {
                this.profileEditForm.setInitialPhoto(photo)
            }
            isPhotoLoaded = true
            checkIfFullProfileLoaded()
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

    fun selectPhoto() {
        _selectPhoto.value = Event("")
    }

    fun cityClicked() {
        profileEditForm.filteredPickableCities = profileEditForm.pickableCities
        _city.value = Event("")
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

    fun citySelected(id: Int) {
        val city = profileEditForm.pickableCities.firstOrNull { cv -> cv.id == id }
        if (city != null) {
            profileEditForm.city = city
        }
    }

    fun saveChanges(onComplete: () -> Unit = {}) {
        if (profileEditForm.areValuesChanged()) {
            val updatedProfile = UpdateProfile(
                description = profileEditForm.description,
                cityId = profileEditForm.city.id,
                instrumentId = null
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