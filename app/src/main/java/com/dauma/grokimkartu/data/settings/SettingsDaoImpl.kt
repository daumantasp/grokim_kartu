package com.dauma.grokimkartu.data.settings

import com.dauma.grokimkartu.data.settings.entities.DeletePushNotificationsTokenRequest
import com.dauma.grokimkartu.data.settings.entities.PushNotificationsTokenRequest
import com.dauma.grokimkartu.data.settings.entities.SettingsResponse
import com.dauma.grokimkartu.data.settings.entities.UpdateSettingsRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class SettingsDaoImpl(retrofit: Retrofit): SettingsDao {
    private val retrofitSettings: RetrofitSettings = retrofit.create(RetrofitSettings::class.java)

    override fun settings(
        accessToken: String,
        onComplete: (SettingsResponse?, SettingsDaoResponseStatus) -> Unit
    ) {
        retrofitSettings.settings(accessToken).enqueue(object : Callback<SettingsResponse> {
            override fun onResponse(
                call: Call<SettingsResponse>,
                response: Response<SettingsResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val settingsResponseData = response.body()
                        val status = SettingsDaoResponseStatus(true, null)
                        onComplete(settingsResponseData, status)
                    }
                    else -> {
                        val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
                val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun update(
        updateSettingsRequest: UpdateSettingsRequest,
        accessToken: String,
        onComplete: (SettingsResponse?, SettingsDaoResponseStatus) -> Unit
    ) {
        retrofitSettings.update(accessToken, updateSettingsRequest).enqueue(object : Callback<SettingsResponse> {
            override fun onResponse(
                call: Call<SettingsResponse>,
                response: Response<SettingsResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val settingsResponseData = response.body()
                        val status = SettingsDaoResponseStatus(true, null)
                        onComplete(settingsResponseData, status)
                    }
                    500 -> {
                        val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.PROFILE_NOT_FOUND)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
                val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun pushNotificationsToken(
        pushNotificationsTokenRequest: PushNotificationsTokenRequest,
        accessToken: String,
        onComplete: (SettingsDaoResponseStatus) -> Unit
    ) {
        retrofitSettings.token(accessToken, pushNotificationsTokenRequest).enqueue(object : Callback<Array<String>> {
            override fun onResponse(call: Call<Array<String>>, response: Response<Array<String>>) {
                when (response.code()) {
                    200 -> {
                        val status = SettingsDaoResponseStatus(true, null)
                        onComplete(status)
                    }
                    400 -> {
                        val errorBody = response.errorBody()?.string() ?: ""
                        if (errorBody.contains(SettingsDaoResponseStatus.Errors.TOKEN_IS_NOT_PROVIDED.toString(), true)) {
                            val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.TOKEN_IS_NOT_PROVIDED)
                            onComplete(status)
                        } else if (errorBody.contains(SettingsDaoResponseStatus.Errors.DEVICE_ID_IS_NOT_PROVIDED.toString(), true)) {
                            val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.DEVICE_ID_IS_NOT_PROVIDED)
                            onComplete(status)
                        }
                    }
                    else -> {
                        val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(status)
                    }
                }
            }

            override fun onFailure(call: Call<Array<String>>, t: Throwable) {
                val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(status)
            }
        })
    }

    override fun deletePushNotificationsToken(
        deletePushNotificationsTokenRequest: DeletePushNotificationsTokenRequest,
        accessToken: String,
        onComplete: (SettingsDaoResponseStatus) -> Unit
    ) {
        retrofitSettings.token(accessToken, deletePushNotificationsTokenRequest).enqueue(object : Callback<Array<String>> {
            override fun onResponse(call: Call<Array<String>>, response: Response<Array<String>>) {
                when (response.code()) {
                    200 -> {
                        val status = SettingsDaoResponseStatus(true, null)
                        onComplete(status)
                    }
                    400 -> {
                        val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.DEVICE_ID_IS_NOT_PROVIDED)
                        onComplete(status)
                    }
                    else -> {
                        val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(status)
                    }
                }
            }

            override fun onFailure(call: Call<Array<String>>, t: Throwable) {
                val status = SettingsDaoResponseStatus(false, SettingsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(status)
            }
        })
    }

    private interface RetrofitSettings {
        @GET("settings") fun settings(@Header("Authorization") accessToken: String): Call<SettingsResponse>
        @PUT("settings") fun update(@Header("Authorization") accessToken: String, @Body updateRequest: UpdateSettingsRequest): Call<SettingsResponse>
        @PUT("settings/push-notifications/token") fun token(@Header("Authorization") accessToken: String, @Body tokenRequest: PushNotificationsTokenRequest): Call<Array<String>>
        @DELETE("settings/push-notifications/token") fun token(@Header("Authorization") accessToken: String, @Body tokenRequest: DeletePushNotificationsTokenRequest): Call<Array<String>>
    }
}