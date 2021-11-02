package com.dauma.grokimkartu.data.users

import com.dauma.grokimkartu.models.users.AuthenticatedUser
import com.dauma.grokimkartu.models.users.LoginUser
import com.dauma.grokimkartu.models.users.RegistrationUser
import com.dauma.grokimkartu.models.users.User

interface UsersDao {
    fun registerUser(user: RegistrationUser, onComplete: (Boolean, String?, Exception?) -> Unit)
    fun addUserToFirestore(user: User, onComplete: (Boolean, Exception?) -> Unit)
    fun deleteUserFromFirestore(userId: String, onComplete: (Boolean, Exception?) -> Unit)
    fun loginUser(user: LoginUser, onComplete: (Boolean, Exception?) -> Unit)
    fun isUserLoggedIn(): Boolean
    fun logOut()
    fun isEmailVerified(): Boolean
    fun sendEmailVerification()
    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, Exception?) -> Unit)
    fun deleteUser(onComplete: (Boolean, String?, Exception?) -> Unit)
    fun reauthenticateUser(user: LoginUser, onComplete: (Boolean, Exception?) -> Unit)
    fun getAuthenticatedUserDataProfiles(): List<AuthenticatedUser>
    fun updatePassword(newPassword: String, onComplete: (Boolean, Exception?) -> Unit)
}