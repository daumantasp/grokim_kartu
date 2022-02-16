package com.dauma.grokimkartu.viewmodels.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.models.forms.ProfileForm
import com.dauma.grokimkartu.repositories.users.UsersRepository
import com.dauma.grokimkartu.repositories.users.entities.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val profileForm: ProfileForm
) : ViewModel() {
    private val _selectPhoto = MutableLiveData<Event<String>>()
    private val _profileLoaded = MutableLiveData<Event<String>>()
    private val _editInstrument = MutableLiveData<Event<String>>()
    private val _editDescription = MutableLiveData<Event<String>>()
    private val _editCity = MutableLiveData<Event<String>>()
    val selectPhoto: LiveData<Event<String>> = _selectPhoto
    val profileLoaded: LiveData<Event<String>> = _profileLoaded
    val editInstrument: LiveData<Event<String>> = _editInstrument
    val editDescription: LiveData<Event<String>> = _editDescription
    val editCity: LiveData<Event<String>> = _editCity

    companion object {
        private val TAG = "ProfileViewModelImpl"
    }

    fun getProfileForm() : ProfileForm {
        return profileForm
    }

    fun loadProfile() {
        var isProfileLoaded = false
        var isPhotoLoaded = false
        fun checkIfFullProfileLoaded() {
            if (isProfileLoaded == true && isPhotoLoaded == true) {
                _profileLoaded.value = Event("")
            }
        }

        usersRepository.getUserProfile { profile, e ->
            this.profileForm.setInitialValues(
                profile?.name ?: "",
                profile?.instrument ?: "",
                profile?.description ?: "",
                profile?.city ?: ""
            )
            isProfileLoaded = true
            checkIfFullProfileLoaded()
        }
        usersRepository.getUserPhoto { photo, e ->
            this.profileForm.setInitialPhoto(photo)
            isPhotoLoaded = true
            checkIfFullProfileLoaded()
        }
    }

    fun instrumentClicked() {
        _editInstrument.value = Event("")
    }

    fun descriptionClicked() {
        _editDescription.value = Event("")
    }

    fun cityClicked() {
        _editCity.value = Event("")
    }

    fun saveChanges(onComplete: () -> Unit = {}) {
        if (profileForm.areValuesChanged() == true) {
            val newProfile = Profile(
                profileForm.name,
                profileForm.instrument,
                profileForm.description,
                profileForm.city
            )

            usersRepository.setUserProfile(newProfile) { isSuccessful, e ->
                if (isSuccessful) {
                    Log.d(TAG, "User profile updated successfully")
                    this.profileForm.setInitialValues(
                        newProfile.name ?: "",
                        newProfile.instrument ?: "",
                        newProfile.description ?: "",
                        newProfile.city ?: ""
                    )
                }
                onComplete()
            }
        } else if (profileForm.isPhotoChanged() == true) {
            if (profileForm.photo != null) {
                usersRepository.setUserPhoto(this.profileForm.photo!!) { isSuccessful, e ->
                    this.profileForm.setInitialPhoto(this.profileForm.photo!!)
                    onComplete()
                }
            } else {
                onComplete()
            }
        } else {
            onComplete()
        }

        // TODO: onComplete should be called only when all data is set
    }

    fun selectPhoto() {
        _selectPhoto.value = Event("")
    }
}