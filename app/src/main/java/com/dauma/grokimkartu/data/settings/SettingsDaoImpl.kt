package com.dauma.grokimkartu.data.settings

import com.dauma.grokimkartu.data.settings.entities.SettingsResponse
import com.dauma.grokimkartu.data.settings.entities.UpdateSettingsRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

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

    private interface RetrofitSettings {
        @GET("settings") fun settings(@Header("Authorization") accessToken: String): Call<SettingsResponse>
        @PUT("settings") fun update(@Header("Authorization") accessToken: String, @Body updateRequest: UpdateSettingsRequest): Call<SettingsResponse>
    }
}