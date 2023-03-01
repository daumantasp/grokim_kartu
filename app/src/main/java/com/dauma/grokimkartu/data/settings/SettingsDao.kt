package com.dauma.grokimkartu.data.settings

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.settings.entities.DeletePushNotificationsTokenRequest
import com.dauma.grokimkartu.data.settings.entities.PushNotificationsTokenRequest
import com.dauma.grokimkartu.data.settings.entities.SettingsResponse
import com.dauma.grokimkartu.data.settings.entities.UpdateSettingsRequest

interface SettingsDao {
    suspend fun settings(accessToken: String): DaoResult<SettingsResponse?, SettingsDaoResponseStatus>
    suspend fun update(updateSettingsRequest: UpdateSettingsRequest, accessToken: String): DaoResult<SettingsResponse?, SettingsDaoResponseStatus>
    suspend fun pushNotificationsToken(pushNotificationsTokenRequest: PushNotificationsTokenRequest, accessToken: String): DaoResult<Nothing?, SettingsDaoResponseStatus>
    suspend fun deletePushNotificationsToken(deletePushNotificationsTokenRequest: DeletePushNotificationsTokenRequest, accessToken: String): DaoResult<Nothing?, SettingsDaoResponseStatus>
}