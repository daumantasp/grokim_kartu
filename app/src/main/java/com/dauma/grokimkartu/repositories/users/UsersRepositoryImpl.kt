package com.dauma.grokimkartu.repositories.users

import com.dauma.grokimkartu.data.users.UsersDao
import com.dauma.grokimkartu.models.users.LoginUser
import com.dauma.grokimkartu.models.users.RegistrationUser
import com.dauma.grokimkartu.models.users.User
import java.lang.Exception

class UsersRepositoryImpl(private val usersDao: UsersDao) : UsersRepository {
    override fun isUserLoggedIn(): Boolean {
        return usersDao.isUserLoggedIn()
    }

    override fun registerUser(user: RegistrationUser, onComplete: (Boolean, User?) -> Unit) {
        if (isUserLoggedIn() == false) {
            usersDao.registerUser(user, onComplete)
        } else {
            throw AuthenticationException(1, "User is already logged in!")
        }
    }

    override fun sendEmailVerification() {
        if (isUserLoggedIn()) {
            usersDao.sendEmailVerification()
        } else {
            throw AuthenticationException(2, "User is not logged in!")
        }
    }

    override fun isEmailVerified(): Boolean {
        if (isUserLoggedIn()) {
            return usersDao.isEmailVerified()
        } else {
            throw AuthenticationException(2, "User is not logged in!")
        }
    }

    override fun loginUser(user: LoginUser, onComplete: (Boolean) -> Unit) {
        if (isUserLoggedIn() == false) {
            usersDao.loginUser(user, onComplete)
        } else {
            throw AuthenticationException(1, "User is already logged in!")
        }
    }

    override fun logOut() {
        if (isUserLoggedIn()) {
            usersDao.logOut()
        } else {
            throw AuthenticationException(2, "User is not logged in!")
        }
    }
}

class AuthenticationException(code: Int, message: String)
    : Exception(message) {
}