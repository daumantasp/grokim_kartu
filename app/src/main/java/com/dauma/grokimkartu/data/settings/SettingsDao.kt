package com.dauma.grokimkartu.data.settings

import com.dauma.grokimkartu.data.settings.entities.DeletePushNotificationsTokenRequest
import com.dauma.grokimkartu.data.settings.entities.PushNotificationsTokenRequest
import com.dauma.grokimkartu.data.settings.entities.SettingsResponse
import com.dauma.grokimkartu.data.settings.entities.UpdateSettingsRequest

interface SettingsDao {
    fun settings(accessToken: String, onComplete: (SettingsResponse?, SettingsDaoResponseStatus) -> Unit)
    fun update(updateSettingsRequest: UpdateSettingsRequest, accessToken: String, onComplete: (SettingsResponse?, SettingsDaoResponseStatus) -> Unit)
    fun pushNotificationsToken(pushNotificationsTokenRequest: PushNotificationsTokenRequest, accessToken: String, onComplete: (SettingsDaoResponseStatus) -> Unit)
    fun deletePushNotificationsToken(deletePushNotificationsTokenRequest: DeletePushNotificationsTokenRequest, accessToken: String, onComplete: (SettingsDaoResponseStatus) -> Unit)
}