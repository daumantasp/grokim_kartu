package com.dauma.grokimkartu.repositories.settings

import com.dauma.grokimkartu.data.settings.SettingsDao
import com.dauma.grokimkartu.data.settings.entities.SettingsResponse
import com.dauma.grokimkartu.data.settings.entities.UpdateSettingsRequest
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.settings.entities.Settings

class SettingsRepositoryImpl(
    private val settingsDao: SettingsDao,
    private val user: User
): SettingsRepository {
    override fun settings(onComplete: (Settings?, SettingsErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            settingsDao.settings(user.getBearerAccessToken()!!) { settingsResponse, settingsDaoResponseStatus ->
                if (settingsDaoResponseStatus.isSuccessful && settingsResponse != null) {
                    val settings = toSettings(settingsResponse)
                    onComplete(settings, null)
                } else {
                    onComplete(null, SettingsErrors.UNKNOWN)
                }
            }
        } else {
            throw SettingsException(SettingsErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun update(settings: Settings, onComplete: (Settings?, SettingsErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            val updateSettingsRequest = UpdateSettingsRequest(
                isVisible = settings.isVisible
            )
            settingsDao.update(updateSettingsRequest, user.getBearerAccessToken()!!) { settingsResponse, settingsDaoResponseStatus ->
                if (settingsDaoResponseStatus.isSuccessful && settingsResponse != null) {
                    val updatedSettings = toSettings(settingsResponse)
                    onComplete(updatedSettings, null)
                } else {
                    onComplete(null, SettingsErrors.UNKNOWN)
                }
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