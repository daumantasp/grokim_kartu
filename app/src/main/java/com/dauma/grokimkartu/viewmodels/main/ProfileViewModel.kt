package com.dauma.grokimkartu.viewmodels.main

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.models.forms.ProfileForm
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.repositories.users.UsersRepository
import com.dauma.grokimkartu.repositories.users.entities.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val profileForm: ProfileForm
) : ViewModel() {
    private val _navigateToLogin = MutableLiveData<Event<String>>()
    private val _selectPhoto = MutableLiveData<Event<String>>()
    val navigateToLogin: LiveData<Event<String>> = _navigateToLogin
    val selectPhoto: LiveData<Event<String>> = _selectPhoto

    companion object {
        private val TAG = "ProfileViewModelImpl"
    }

    fun getProfileForm() : ProfileForm {
        return profileForm
    }

    fun loadProfile() {
        usersRepository.getUserProfile { profile, e ->
            this.profileForm.setInitialValues(
                profile?.name ?: "",
                profile?.instrument ?: "",
                profile?.description ?: "",
                profile?.city ?: ""
            )
        }
        usersRepository.getUserPhoto { photo, e ->
            this.profileForm.setInitialPhoto(photo)
        }
    }

    fun saveChanges() {
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
            }
        }

        if (profileForm.isPhotoChanged() == true) {
            if (profileForm.photo != null) {
                usersRepository.setUserPhoto(this.profileForm.photo!!) { isSuccessful, e ->
                    this.profileForm.setInitialPhoto(this.profileForm.photo!!)
                }
            }
        }
    }

    fun selectPhoto() {
        _selectPhoto.value = Event("")
    }

    fun logoutClicked() {
        try {
            usersRepository.logOut()
            _navigateToLogin.value = Event("")
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "Login was unsuccessful")
        }
    }
}