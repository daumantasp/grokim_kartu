package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.models.forms.ProfileForm
import com.dauma.grokimkartu.repositories.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val profileForm: ProfileForm
) : ViewModel() {
    private val _profileLoaded = MutableLiveData<Event<String>>()
    private val _profileEdit = MutableLiveData<Event<String>>()
    val profileLoaded: LiveData<Event<String>> = _profileLoaded
    val profileEdit: LiveData<Event<String>> = _profileEdit

    companion object {
        private val TAG = "ProfileViewModelImpl"
    }

    fun getProfileForm() : ProfileForm {
        return profileForm
    }

    fun viewIsReady() {
        loadProfile()
    }

    private fun loadProfile() {
        var isProfileLoaded = false
        var isPhotoLoaded = false
        fun checkIfFullProfileLoaded() {
            if (isProfileLoaded == true && isPhotoLoaded == true) {
                _profileLoaded.value = Event("")
            }
        }

        profileRepository.profile { profile, profileErrors ->
            this.profileForm.setValues(
                name = profile?.name,
                instrument = profile?.instrument,
                description = profile?.description,
                city = profile?.city?.name
            )
            isProfileLoaded = true
            checkIfFullProfileLoaded()
        }

        profileRepository.photo() { photo, profileErrors ->
            this.profileForm.photo = photo
            isPhotoLoaded = true
            checkIfFullProfileLoaded()
        }
    }

    fun editClicked() {
        _profileEdit.value = Event("")
    }
}