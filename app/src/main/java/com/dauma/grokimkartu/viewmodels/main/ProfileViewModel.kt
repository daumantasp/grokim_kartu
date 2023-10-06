package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.models.forms.ProfileForm
import com.dauma.grokimkartu.repositories.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val profileForm: ProfileForm
) : ViewModel() {
    init {
        loadProfile()
    }

    fun getProfileForm() : ProfileForm {
        return profileForm
    }

    private fun loadProfile() {
//        profileRepository.profile { profile, _ ->
//            profileForm.setValues(
//                name = profile?.name,
//                instrument = profile?.instrument?.name,
//                description = profile?.description,
//                city = profile?.city?.name
//            )
//        }
//
//        profileRepository.photo { photo, _ ->
//            profileForm.photo = photo
//        }
    }
}