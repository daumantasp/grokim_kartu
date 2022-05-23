package com.dauma.grokimkartu.repositories.settings

import com.dauma.grokimkartu.repositories.settings.entities.Settings

interface SettingsRepository {
    fun settings(onComplete: (Settings?, SettingsErrors?) -> Unit)
    fun update(settings: Settings, onComplete: (Settings?, SettingsErrors?) -> Unit)
}