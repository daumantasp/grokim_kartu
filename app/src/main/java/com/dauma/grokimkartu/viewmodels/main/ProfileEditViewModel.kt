package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.models.forms.ProfileEditForm
import com.dauma.grokimkartu.repositories.users.UsersRepository
import com.dauma.grokimkartu.repositories.users.entities.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val profileEditForm: ProfileEditForm,
    private val utils: Utils
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _selectPhoto = MutableLiveData<Event<String>>()
    private val _profileLoaded = MutableLiveData<Event<String>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val selectPhoto: LiveData<Event<String>> = _selectPhoto
    val profileLoaded: LiveData<Event<String>> = _profileLoaded

    fun viewIsReady() {
        loadProfile()
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
            this.profileEditForm.setInitialValues(
                profile?.name ?: "",
                profile?.instrument ?: "",
                profile?.description ?: "",
                profile?.city ?: ""
            )
            isProfileLoaded = true
            checkIfFullProfileLoaded()
        }
        usersRepository.getUserPhoto { photo, exception ->
            this.profileEditForm.setInitialPhoto(photo)
            isPhotoLoaded = true
            checkIfFullProfileLoaded()
        }
    }

    fun selectPhoto() {
        _selectPhoto.value = Event("")
    }

    fun saveChanges(onComplete: () -> Unit = {}) {
        if (profileEditForm.areValuesChanged()) {
            val newProfile = Profile(
                name = null,
                instrument = profileEditForm.instrument,
                description = profileEditForm.description,
                city = profileEditForm.city
            )

            val name = profileEditForm.name
            usersRepository.setUserProfile(newProfile) { isSuccessful, exception ->
                if (isSuccessful) {
                    this.profileEditForm.setInitialValues(
                        name = name,
                        instrument = newProfile.instrument ?: "",
                        description = newProfile.description ?: "",
                        city = newProfile.city ?: ""
                    )
                }

                // Photo uploading should be considerably longer
                if (profileEditForm.isPhotoChanged() == false) {
                    onComplete()
                }
            }
        }
        if (profileEditForm.isPhotoChanged()) {
            if (profileEditForm.photo != null) {
                usersRepository.setUserPhoto(this.profileEditForm.photo!!) { isSuccessful, e ->
                    this.profileEditForm.setInitialPhoto(this.profileEditForm.photo!!)
                    onComplete()
                }
            } else {
                onComplete()
            }
        } else {
            onComplete()
        }
    }

    fun getProfileEditForm(): ProfileEditForm {
        return profileEditForm
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }
}