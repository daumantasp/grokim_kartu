package com.dauma.grokimkartu.data.users

import com.dauma.grokimkartu.models.users.LoginUser
import com.dauma.grokimkartu.models.users.RegistrationUser
import com.dauma.grokimkartu.models.users.User
import java.lang.Exception

interface UsersDao {
    fun registerUser(user: RegistrationUser, onComplete: (Boolean, User?) -> Unit)
    fun loginUser(user: LoginUser, onComplete: (Boolean, Exception?) -> Unit)
    fun isUserLoggedIn(): Boolean
    fun logOut()
    fun isEmailVerified(): Boolean
    fun sendEmailVerification()
}