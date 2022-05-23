package com.dauma.grokimkartu.data.settings

import com.dauma.grokimkartu.data.settings.entities.SettingsResponse
import com.dauma.grokimkartu.data.settings.entities.UpdateSettingsRequest

interface SettingsDao {
    fun settings(accessToken: String, onComplete: (SettingsResponse?, SettingsDaoResponseStatus) -> Unit)
    fun update(updateSettingsRequest: UpdateSettingsRequest, accessToken: String, onComplete: (SettingsResponse?, SettingsDaoResponseStatus) -> Unit)
}