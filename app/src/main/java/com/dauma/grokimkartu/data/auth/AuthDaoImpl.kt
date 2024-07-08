package com.dauma.grokimkartu.data.auth

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.auth.entities.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class AuthDaoImpl(retrofit: Retrofit) : AuthDao {
    private val retrofitAuth: RetrofitAuth = retrofit.create(RetrofitAuth::class.java)

    override suspend fun register(registrationRequest: RegistrationRequest): DaoResult<LoginResponse?, AuthDaoResponseStatus> {
        val response = retrofitAuth.register(registrationRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                201 -> {
                    val registrationResponse = response.body()
                    val status = AuthDaoResponseStatus(true, null)
                    return DaoResult(registrationResponse, status)
                }
                else -> { throw Exception("UNEXPECTED SERVER SUCCESS RESPONSE") }
            }
        } else {
            when (response.code()) {
                401 -> {
                    val errorBody = response.errorBody()?.string() ?: ""
                    if (errorBody.contains("The email has already been taken.", true)) {
                        val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.EMAIL_TAKEN)
                        return DaoResult(null, status)
                    } else if (errorBody.contains("The email must be a valid email address.", true)) {
                        val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.INVALID_EMAIL)
                        return DaoResult(null, status)
                    } else if (errorBody.contains("The password confirmation does not match.", true)) {
                        val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.PSW_CONFIRMATION_DONT_MATCH)
                        return DaoResult(null, status)
                    } else {
                        val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                        return DaoResult(null, status)
                    }
                }
                else -> {
                    val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        }
    }

    override suspend fun login(loginRequest: LoginRequest): DaoResult<LoginResponse?, AuthDaoResponseStatus> {
        val response = retrofitAuth.login(loginRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                201 -> {
                    val loginResponse = response.body()
                    val status = AuthDaoResponseStatus(true, null)
                    return DaoResult(loginResponse, status)
                }
                else -> { throw Exception("UNEXPECTED SERVER SUCCESS RESPONSE") }
            }
        } else {
            when (response.code()) {
                401 -> {
                    val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.INCORRECT_USR_NAME_OR_PSW)
                    return DaoResult(null, status)
                }
                403 -> {
                    val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.EMAIL_NOT_VERIFIED)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        }
    }

    override suspend fun reauthenticate(reauthenticateRequest: ReauthenticateRequest): DaoResult<LoginResponse?, AuthDaoResponseStatus> {
        val response = retrofitAuth.tokenLogin(reauthenticateRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                201 -> {
                    val loginResponse = response.body()
                    val status = AuthDaoResponseStatus(true, null)
                    return DaoResult(loginResponse, status)
                }
                else -> { throw Exception("UNEXPECTED SERVER SUCCESS RESPONSE") }
            }
        } else {
            when (response.code()) {
                401 -> {
                    val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.INCORRECT_ACCESS_TOKEN)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        }
    }

    override suspend fun logout(
        logoutRequest: LogoutRequest,
        accessToken: String
    ): DaoResult<Nothing?, AuthDaoResponseStatus> {
        val response = retrofitAuth.logout(accessToken, logoutRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val status = AuthDaoResponseStatus(true, null)
                    return DaoResult(null, status)
                }
                else -> { throw Exception("UNEXPECTED SERVER SUCCESS RESPONSE") }
            }
        } else {
            val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun delete(accessToken: String): DaoResult<Nothing?, AuthDaoResponseStatus> {
        val response = retrofitAuth.delete(accessToken)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val status = AuthDaoResponseStatus(true, null)
                    return DaoResult(null, status)
                }
                else -> { throw Exception("UNEXPECTED SERVER SUCCESS RESPONSE") }
            }
        } else {
            val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun changePassword(
        accessToken: String,
        changePasswordRequest: ChangePasswordRequest
    ): DaoResult<Nothing?, AuthDaoResponseStatus> {
        val response = retrofitAuth.changePassword(accessToken, changePasswordRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val status = AuthDaoResponseStatus(true, null)
                    return DaoResult(null, status)
                }
                else -> { throw Exception("UNEXPECTED SERVER SUCCESS RESPONSE") }
            }
        } else {
            when (response.code()) {
                401 -> {
                    val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.INCORRECT_OLD_PSW)
                    return DaoResult(null, status)
                }
                403 -> {
                    val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.NEW_PSW_SIMILAR)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = AuthDaoResponseStatus(false, AuthDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        }
    }

    private interface RetrofitAuth {
        @POST("register") suspend fun register(@Body registrationRequest: RegistrationRequest): Response<LoginResponse>
        @POST("login") suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
        @POST("reauthenticate") suspend fun tokenLogin(@Body reauthenticateRequest: ReauthenticateRequest): Response<LoginResponse>
        @POST("logout") suspend fun logout(@Header("Authorization") accessToken: String, @Body logoutRequest: LogoutRequest): Response<Array<String>>
        @DELETE("user/deletesuspend ") suspend fun delete(@Header("Authorization") accessToken: String) : Response<Array<String>>
        @POST("user/changepassword") suspend fun changePassword(@Header("Authorization") accessToken: String, @Body changePasswordRequest: ChangePasswordRequest): Response<Array<String>>
    }
}