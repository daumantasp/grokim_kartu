package com.dauma.grokimkartu.data.profile

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.profile.entities.ProfileResponse
import com.dauma.grokimkartu.data.profile.entities.ProfileUnreadCountResponse
import com.dauma.grokimkartu.data.profile.entities.UpdateProfileRequest

interface ProfileDao {
    fun profile(accessToken: String, onComplete: (ProfileResponse?, ProfileDaoResponseStatus) -> Unit)
    fun update(updateProfileRequest: UpdateProfileRequest, accessToken: String, onComplete: (ProfileResponse?, ProfileDaoResponseStatus) -> Unit)
    fun icon(accessToken: String, onComplete: (Bitmap?, ProfileDaoResponseStatus) -> Unit)
    fun photo(accessToken: String, onComplete: (Bitmap?, ProfileDaoResponseStatus) -> Unit)
    fun updatePhoto(accessToken: String, photo: Bitmap, onComplete: (Bitmap?, ProfileDaoResponseStatus) -> Unit)
    fun unreadCount(accessToken: String, onComplete: (ProfileUnreadCountResponse?, ProfileDaoResponseStatus) -> Unit)
}