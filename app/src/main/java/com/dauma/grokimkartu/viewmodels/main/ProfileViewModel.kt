package com.dauma.grokimkartu.viewmodels.main

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.models.Event
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
    private val _selectedPhoto = MutableLiveData<Bitmap>()
    val navigateToLogin: LiveData<Event<String>> = _navigateToLogin
    val selectPhoto: LiveData<Event<String>> = _selectPhoto
    val selectedPhoto: LiveData<Bitmap> = _selectedPhoto

    // TODO refactor
    private var photo: Bitmap? = null

    companion object {
        private val TAG = "ProfileViewModelImpl"
    }

    fun getProfileForm() : ProfileForm {
        return profileForm
    }

    fun loadProfile() {
        usersRepository.getUserProfile { profile, e ->
            this.profileForm.setInitialValues(
                profile?.instrument ?: "",
                profile?.description ?: ""
            )
            if (profile?.photo != null) {
                _selectedPhoto.value = profile.photo!!
            }
        }
    }

    fun saveChanges() {
        if (profileForm.isChanged() == false) {
            return
        }

        val newProfile = Profile(
            profileForm.instrument,
            profileForm.description,
            photo
        )

        usersRepository.setUserProfile(newProfile) { isSuccessful, e ->
            if (isSuccessful) {
                Log.d(TAG, "User profile updated successfully")
                this.profileForm.setInitialValues(
                    newProfile.instrument ?: "",
                    newProfile.description ?: ""
                )
            }
        }
    }

    fun selectPhoto() {
        _selectPhoto.value = Event("")
    }

    fun photoSelected(photo: Bitmap) {
        this.photo = photo
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