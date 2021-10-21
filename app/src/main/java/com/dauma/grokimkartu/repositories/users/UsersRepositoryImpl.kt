package com.dauma.grokimkartu.repositories.users

import com.dauma.grokimkartu.data.users.UsersDao
import com.dauma.grokimkartu.models.users.LoginUser
import com.dauma.grokimkartu.models.users.RegistrationUser
import com.dauma.grokimkartu.models.users.User
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.lang.Exception

class UsersRepositoryImpl(private val usersDao: UsersDao) : UsersRepository {
    override fun isUserLoggedIn(): Boolean {
        return usersDao.isUserLoggedIn()
    }

    override fun registerUser(user: RegistrationUser, onComplete: (Boolean, AuthenticationError?) -> Unit) {
        if (isUserLoggedIn() == false) {
            usersDao.registerUser(user) { isSuccessful, userId, e ->
                if (isSuccessful) {
                    val userToSaveInFirestore = User(userId!!, user.name)
                    this.usersDao.addUserToFirestore(userToSaveInFirestore) { isSuccessful, e ->
                        if (isSuccessful) {
                            onComplete(true, null)
                        } else {
                            val error = AuthenticationError(6, "Failed adding registered user to Firestore!")
                            onComplete(false, error)
                        }
                    }
                } else {
                    val error: AuthenticationError
                    if (e is FirebaseAuthUserCollisionException) {
                        error = AuthenticationError(7, "Email already registered!")
                    } else if (e is FirebaseAuthInvalidCredentialsException) {
                        error = AuthenticationError(8, "Email is in incorrect format!")
                    } else if (e is FirebaseAuthWeakPasswordException) {
                        error = AuthenticationError(9, "Password is too weak!")
                    } else {
                        error = AuthenticationError(5, "Something failed")
                    }
                    onComplete(false, error)
                }
            }
        } else {
            val error = AuthenticationError(1, "User is already logged in!")
            throw AuthenticationException(error)
        }
    }

    override fun sendEmailVerification() {
        if (isUserLoggedIn()) {
            usersDao.sendEmailVerification()
        } else {
            val error = AuthenticationError(2, "User is not logged in!")
            throw AuthenticationException(error)
        }
    }

    override fun isEmailVerified(): Boolean {
        if (isUserLoggedIn()) {
            return usersDao.isEmailVerified()
        } else {
            val error = AuthenticationError(2, "User is not logged in!")
            throw AuthenticationException(error)
        }
    }

    override fun loginUser(user: LoginUser, onComplete: (Boolean, AuthenticationError?) -> Unit) {
        if (isUserLoggedIn() == false) {
            usersDao.loginUser(user) { isSuccessful, e ->
                if (isSuccessful) {
                    onComplete(true, null)
                } else {
                    val error: AuthenticationError
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        error = AuthenticationError(3, "Invalid password")
                    } else if (e is FirebaseAuthInvalidUserException) {
                        error = AuthenticationError(4, "Incorrect email address")
                    } else {
                        error = AuthenticationError(5, "Something failed")
                    }
                    onComplete(false, error)
                }
            }
        } else {
            val error = AuthenticationError(1, "User is already logged in!")
            throw AuthenticationException(error)
        }
        // READ MORE AT: https://www.techotopia.com/index.php?title=Handling_Firebase_Authentication_Errors_and_Failures&mobileaction=toggle_view_mobile
    }

    override fun logOut() {
        if (isUserLoggedIn()) {
            usersDao.logOut()
        } else {
            val error = AuthenticationError(2, "User is not logged in!")
            throw AuthenticationException(error)
        }
    }
}

class AuthenticationException(error: AuthenticationError)
    : Exception(error.message) {
}

data class AuthenticationError(val code: Int, val message: String)