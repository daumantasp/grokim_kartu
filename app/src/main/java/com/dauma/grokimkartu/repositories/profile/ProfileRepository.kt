package com.dauma.grokimkartu.repositories.profile

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.profile.entities.*
import com.dauma.grokimkartu.repositories.Result
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val unreadCount: StateFlow<ProfileUnreadCount?>
    suspend fun profile(): Result<Profile?, ProfileErrors?>
    suspend fun cities(): Result<List<ProfileCity>?, ProfileErrors?>
    suspend fun searchCity(value: String): Result<List<ProfileCity>?, ProfileErrors?>
    suspend fun instruments(): Result<List<ProfileInstrument>?, ProfileErrors?>
    suspend fun searchInstrument(value: String): Result<List<ProfileInstrument>?, ProfileErrors?>
    suspend fun update(updateProfile: UpdateProfile): Result<Profile?, ProfileErrors?>
    suspend fun icon(): Result<Bitmap?, ProfileErrors?>
    suspend fun photo(): Result<Bitmap?, ProfileErrors?>
    suspend fun updatePhoto(photo: Bitmap): Result<Bitmap?, ProfileErrors?>
    suspend fun reloadUnreadCount()
    fun loginCompleted(isSuccessful: Boolean)
    fun logoutCompleted(isSuccessful: Boolean)
}