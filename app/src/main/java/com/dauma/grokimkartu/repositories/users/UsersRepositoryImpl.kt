package com.dauma.grokimkartu.repositories.users

import com.dauma.grokimkartu.data.users.UsersDao
import com.dauma.grokimkartu.models.users.LoginUser
import com.dauma.grokimkartu.models.users.RegistrationUser
import com.dauma.grokimkartu.models.users.User

class UsersRepositoryImpl(private val usersDao: UsersDao) : UsersRepository {
    override fun registerUser(user: RegistrationUser, onComplete: (Boolean, User?) -> Unit) {
        usersDao.registerUser(user, onComplete)
    }

    override fun loginUser(user: LoginUser, onComplete: (Boolean) -> Unit) {
        usersDao.loginUser(user, onComplete)
    }
}