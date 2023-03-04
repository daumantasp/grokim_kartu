package com.dauma.grokimkartu.data.profile

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.profile.entities.ProfileResponse
import com.dauma.grokimkartu.data.profile.entities.ProfileUnreadCountResponse
import com.dauma.grokimkartu.data.profile.entities.UpdateProfileRequest

interface ProfileDao {
    suspend fun profile(accessToken: String): DaoResult<ProfileResponse?, ProfileDaoResponseStatus>
    suspend fun update(updateProfileRequest: UpdateProfileRequest, accessToken: String): DaoResult<ProfileResponse?, ProfileDaoResponseStatus>
    suspend fun icon(accessToken: String): DaoResult<Bitmap?, ProfileDaoResponseStatus>
    suspend fun photo(accessToken: String): DaoResult<Bitmap?, ProfileDaoResponseStatus>
    suspend fun updatePhoto(accessToken: String, photo: Bitmap): DaoResult<Bitmap?, ProfileDaoResponseStatus>
    suspend fun unreadCount(accessToken: String): DaoResult<ProfileUnreadCountResponse?, ProfileDaoResponseStatus>
}