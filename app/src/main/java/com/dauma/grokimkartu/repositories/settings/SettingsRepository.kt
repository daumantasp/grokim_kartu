package com.dauma.grokimkartu.repositories.settings

import com.dauma.grokimkartu.repositories.settings.entities.Settings
import com.dauma.grokimkartu.repositories.Result

interface SettingsRepository {
    suspend fun settings(): Result<Settings?, SettingsErrors?>
    suspend fun update(settings: Settings): Result<Settings?, SettingsErrors?>
}