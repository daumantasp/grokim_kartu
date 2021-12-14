package com.dauma.grokimkartu.viewmodels.main

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
    val navigateToLogin: LiveData<Event<String>> = _navigateToLogin

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
        }
    }

    fun saveChanges() {
        if (profileForm.isChanged() == false) {
            return
        }

        val newProfile = Profile(
            profileForm.instrument,
            profileForm.description
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

    fun logoutClicked() {
        try {
            usersRepository.logOut()
            _navigateToLogin.value = Event("")
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "Login was unsuccessful")
        }
    }
}