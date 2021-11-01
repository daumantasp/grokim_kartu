package com.dauma.grokimkartu.repositories.users

import com.dauma.grokimkartu.models.users.LoginUser
import com.dauma.grokimkartu.models.users.RegistrationUser
import com.dauma.grokimkartu.models.users.User

interface UsersRepository {
    fun isUserLoggedIn(): Boolean
    fun registerUser(user: RegistrationUser, onComplete: (Boolean, AuthenticationError?) -> Unit)
    fun sendEmailVerification()
    fun isEmailVerified(): Boolean
    fun loginUser(user: LoginUser, onComplete: (Boolean, AuthenticationError?) -> Unit)
    fun logOut()
    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, AuthenticationError?) -> Unit)
}