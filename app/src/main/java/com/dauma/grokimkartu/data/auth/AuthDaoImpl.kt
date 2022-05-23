package com.dauma.grokimkartu.data.auth

import com.dauma.grokimkartu.data.auth.entities.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class AuthDaoImpl(retrofit: Retrofit) : AuthDao {
    private val retrofitAuth: RetrofitAuth = retrofit.create(RetrofitAuth::class.java)

    override fun register(
        registrationRequest: RegistrationRequest,
        onComplete: (LoginResponse?, AuthDaoResponseStatus) -> Unit
    ) {
        retrofitAuth.register(registrationRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                when (response.code()) {
                    201 -> {
                        val registrationResponse = response.body()
                        val status = AuthDaoResponseStatus(true, null)
                        onComplete(registrationResponse, status)
                    }
                    422 -> {
                        val errorBody = response.errorBody()?.string() ?: ""
                        if (errorBody.contains("The email has already been taken.", true)) {
                            val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.EMAIL_TAKEN)
                            onComplete(null, status)
                        } else if (errorBody.contains("The email must be a valid email address.", true)) {
                            val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.INVALID_EMAIL)
                            onComplete(null, status)
                        } else if (errorBody.contains("The password confirmation does not match.", true)) {
                            val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.PSW_CONFIRMATION_DONT_MATCH)
                            onComplete(null, status)
                        }
                    }
                    else -> {
                        val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun login(
        loginRequest: LoginRequest,
        onComplete: (LoginResponse?, AuthDaoResponseStatus) -> Unit
    ) {
        retrofitAuth.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                when (response.code()) {
                    201 -> {
                        val loginResponse = response.body()
                        val status = AuthDaoResponseStatus(true, null)
                        onComplete(loginResponse, status)
                    }
                    422 -> {
                        val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.INCORRECT_USR_NAME_OR_PSW)
                        onComplete(null, status)
                    }
                    403 -> {
                        val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.EMAIL_NOT_VERIFIED)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun logout(
        accessToken: String,
        onComplete: (AuthDaoResponseStatus) -> Unit
    ) {
        retrofitAuth.logout(accessToken).enqueue(object : Callback<Array<String>> {
            override fun onResponse(call: Call<Array<String>>, response: Response<Array<String>>) {
                when (response.code()) {
                    200 -> {
                        val status = AuthDaoResponseStatus(true, null)
                        onComplete(status)
                    }
                    else -> {
                        val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(status)
                    }
                }
            }

            override fun onFailure(call: Call<Array<String>>, t: Throwable) {
                val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                onComplete(status)
            }
        })
    }

    override fun delete(
        accessToken: String,
        onComplete: (AuthDaoResponseStatus) -> Unit
    ) {
        retrofitAuth.delete(accessToken).enqueue(object : Callback<Array<String>> {
            override fun onResponse(call: Call<Array<String>>, response: Response<Array<String>>) {
                when (response.code()) {
                    200 -> {
                        val status = AuthDaoResponseStatus(true, null)
                        onComplete(status)
                    }
                    else -> {
                        val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(status)
                    }
                }
            }

            override fun onFailure(call: Call<Array<String>>, t: Throwable) {
                val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                onComplete(status)
            }
        })
    }

    override fun changePassword(
        accessToken: String,
        changePasswordRequest: ChangePasswordRequest,
        onComplete: (AuthDaoResponseStatus) -> Unit
    ) {
        retrofitAuth.changePassword(accessToken, changePasswordRequest).enqueue(object : Callback<Array<String>> {
            override fun onResponse(call: Call<Array<String>>, response: Response<Array<String>>) {
                when (response.code()) {
                    200 -> {
                        val status = AuthDaoResponseStatus(true, null)
                        onComplete(status)
                    }
                    401 -> {
                        val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.INCORRECT_OLD_PSW)
                        onComplete(status)
                    }
                    403 -> {
                        val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.NEW_PSW_SIMILAR)
                        onComplete(status)
                    }
                    else -> {
                        val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(status)
                    }
                }
            }

            override fun onFailure(call: Call<Array<String>>, t: Throwable) {
                val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                onComplete(status)
            }
        })
    }

    private interface RetrofitAuth {
        @POST("register") fun register(@Body registrationRequest: RegistrationRequest): Call<LoginResponse>
        @POST("login") fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
        @POST("logout") fun logout(@Header("Authorization") accessToken: String): Call<Array<String>>
        @DELETE("user/delete") fun delete(@Header("Authorization") accessToken: String) : Call<Array<String>>
        @POST("user/changepassword") fun changePassword(@Header("Authorization") accessToken: String, @Body changePasswordRequest: ChangePasswordRequest): Call<Array<String>>
    }
}