package com.dauma.grokimkartu.repositories.profile

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.profile.entities.Profile

interface ProfileRepository {
    fun profile(onComplete: (Profile?, ProfileErrors?) -> Unit)
    fun update(profile: Profile, onComplete: (Profile?, ProfileErrors?) -> Unit)
    fun icon(onComplete: (Bitmap?, ProfileErrors?) -> Unit)
    fun photo(onComplete: (Bitmap?, ProfileErrors?) -> Unit)
    fun updatePhoto(photo: Bitmap, onComplete: (Bitmap?, ProfileErrors?) -> Unit)
}