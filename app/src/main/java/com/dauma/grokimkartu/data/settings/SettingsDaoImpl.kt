package com.dauma.grokimkartu.data.settings

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.settings.entities.DeletePushNotificationsTokenRequest
import com.dauma.grokimkartu.data.settings.entities.PushNotificationsTokenRequest
import com.dauma.grokimkartu.data.settings.entities.SettingsResponse
import com.dauma.grokimkartu.data.settings.entities.UpdateSettingsRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class SettingsDaoImpl(retrofit: Retrofit): SettingsDao {
    private val retrofitSettings: RetrofitSettings = retrofit.create(RetrofitSettings::class.java)

    override suspend fun settings(accessToken: String): DaoResult<SettingsResponse?, SettingsDaoResponseStatus> {
        val response = retrofitSettings.settings(accessToken)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val settingsResponseData = response.body()
                    val status = SettingsDaoResponseStatus(true, null)
                    return DaoResult(settingsResponseData, status)
                }
                else -> {
                    val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun update(
        updateSettingsRequest: UpdateSettingsRequest,
        accessToken: String
    ): DaoResult<SettingsResponse?, SettingsDaoResponseStatus> {
        val response = retrofitSettings.update(accessToken, updateSettingsRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val settingsResponseData = response.body()
                    val status = SettingsDaoResponseStatus(true, null)
                    return DaoResult(settingsResponseData, status)
                }
                500 -> {
                    val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.PROFILE_NOT_FOUND)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun pushNotificationsToken(
        pushNotificationsTokenRequest: PushNotificationsTokenRequest,
        accessToken: String
    ): DaoResult<Nothing?, SettingsDaoResponseStatus> {
        val response = retrofitSettings.token(accessToken, pushNotificationsTokenRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val status = SettingsDaoResponseStatus(true, null)
                    return DaoResult(null, status)
                }
                400 -> {
                    val errorBody = response.errorBody()?.string() ?: ""
                    if (errorBody.contains(SettingsDaoResponseStatus.Errors.TOKEN_IS_NOT_PROVIDED.toString(), true)) {
                        val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.TOKEN_IS_NOT_PROVIDED)
                        return DaoResult(null, status)
                    } else if (errorBody.contains(SettingsDaoResponseStatus.Errors.DEVICE_ID_IS_NOT_PROVIDED.toString(), true)) {
                        val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.DEVICE_ID_IS_NOT_PROVIDED)
                        return DaoResult(null, status)
                    } else {
                        val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
                        return DaoResult(null, status)
                    }
                }
                else -> {
                    val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun deletePushNotificationsToken(
        deletePushNotificationsTokenRequest: DeletePushNotificationsTokenRequest,
        accessToken: String
    ): DaoResult<Nothing?, SettingsDaoResponseStatus> {
        val response = retrofitSettings.token(accessToken, deletePushNotificationsTokenRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val status = SettingsDaoResponseStatus(true, null)
                    return DaoResult(null, status)
                }
                400 -> {
                    val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.DEVICE_ID_IS_NOT_PROVIDED)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    private interface RetrofitSettings {
        @GET("settings") suspend fun settings(@Header("Authorization") accessToken: String): Response<SettingsResponse>
        @PUT("settings") suspend fun update(@Header("Authorization") accessToken: String, @Body updateRequest: UpdateSettingsRequest): Response<SettingsResponse>
        @PUT("settings/push-notifications/token") suspend fun token(@Header("Authorization") accessToken: String, @Body tokenRequest: PushNotificationsTokenRequest): Response<Array<String>>
        @DELETE("settings/push-notifications/token") suspend fun token(@Header("Authorization") accessToken: String, @Body tokenRequest: DeletePushNotificationsTokenRequest): Response<Array<String>>
    }
}