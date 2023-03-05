package com.dauma.grokimkartu.repositories.settings

import com.dauma.grokimkartu.data.settings.SettingsDao
import com.dauma.grokimkartu.data.settings.entities.SettingsResponse
import com.dauma.grokimkartu.data.settings.entities.UpdateSettingsRequest
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.settings.entities.Settings
import com.dauma.grokimkartu.repositories.Result

class SettingsRepositoryImpl(
    private val settingsDao: SettingsDao,
    private val user: User
): SettingsRepository {
    override suspend fun settings(): Result<Settings?, SettingsErrors?> {
        if (user.isUserLoggedIn()) {
            val response = settingsDao.settings(user.getBearerAccessToken()!!)
            val status = response.status
            val settingsResponse = response.data
            if (status.isSuccessful && settingsResponse != null) {
                val settings = toSettings(settingsResponse)
                return Result(settings, null)
            } else {
                return Result(null, SettingsErrors.UNKNOWN)
            }
        } else {
            throw SettingsException(SettingsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun update(settings: Settings): Result<Settings?, SettingsErrors?> {
        if (user.isUserLoggedIn()) {
            val updateSettingsRequest = UpdateSettingsRequest(isVisible = settings.isVisible)
            val response = settingsDao.update(updateSettingsRequest, user.getBearerAccessToken()!!)
            val status = response.status
            val settingsResponse = response.data
            if (status.isSuccessful && settingsResponse != null) {
                val updatedSettings = toSettings(settingsResponse)
                return Result(updatedSettings, null)
            } else {
                return Result(null, SettingsErrors.UNKNOWN)
            }
        } else {
            throw SettingsException(SettingsErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun toSettings(settingsResponse: SettingsResponse): Settings {
        return Settings(
            name = settingsResponse.name,
            email = settingsResponse.email,
            createdAt = settingsResponse.createdAt,
            isVisible = settingsResponse.isVisible
        )
    }
}