package com.dauma.grokimkartu.repositories.profile

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.profile.entities.Profile
import com.dauma.grokimkartu.repositories.profile.entities.ProfileCity
import com.dauma.grokimkartu.repositories.profile.entities.ProfileInstrument
import com.dauma.grokimkartu.repositories.profile.entities.UpdateProfile

interface ProfileRepository {
    fun profile(onComplete: (Profile?, ProfileErrors?) -> Unit)
    fun cities(onComplete: (List<ProfileCity>?, ProfileErrors?) -> Unit)
    fun searchCity(value: String, onComplete: (List<ProfileCity>?, ProfileErrors?) -> Unit)
    fun instruments(onComplete: (List<ProfileInstrument>?, ProfileErrors?) -> Unit)
    fun searchInstrument(value: String, onComplete: (List<ProfileInstrument>?, ProfileErrors?) -> Unit)
    fun update(updateProfile: UpdateProfile, onComplete: (Profile?, ProfileErrors?) -> Unit)
    fun icon(onComplete: (Bitmap?, ProfileErrors?) -> Unit)
    fun photo(onComplete: (Bitmap?, ProfileErrors?) -> Unit)
    fun updatePhoto(photo: Bitmap, onComplete: (Bitmap?, ProfileErrors?) -> Unit)
}