package com.dauma.grokimkartu.general.user

import com.dauma.grokimkartu.data.auth.entities.LoginResponse
import java.sql.Timestamp

class User {
    var id: Int? = null
    var name: String? = null
    var email: String? = null
    var isEmailVerified: Boolean? = null
    var createdAt: Timestamp? = null
    var photoId: String? = null
    var accessToken: String? = null

    fun isUserLoggedIn(): Boolean {
        return accessToken.isNullOrEmpty() == false
    }

    fun getBearerAccessToken(): String? {
        if (accessToken != null) {
            return "Bearer $accessToken"
        }
        return null
    }

    fun login(loginResponse: LoginResponse) {
        id = loginResponse.user?.id
        name = loginResponse.user?.name
        email = loginResponse.user?.email
        isEmailVerified = loginResponse.user?.isEmailVerified
        createdAt = loginResponse.user?.createdAt
        photoId = loginResponse.user?.photoId
        accessToken = loginResponse.accessToken
    }

    fun logout() {
        id = null
        name = null
        email = null
        isEmailVerified = null
        createdAt = null
        photoId = null
        accessToken = null
    }
}