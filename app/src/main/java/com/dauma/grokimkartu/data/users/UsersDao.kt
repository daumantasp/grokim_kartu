package com.dauma.grokimkartu.data.users

import com.dauma.grokimkartu.models.users.LoginUser
import com.dauma.grokimkartu.models.users.RegistrationUser
import com.dauma.grokimkartu.models.users.User

interface UsersDao {
    fun registerUser(user: RegistrationUser, onComplete: (Boolean, User?) -> Unit)
    fun loginUser(user: LoginUser, onComplete: (Boolean) -> Unit)
}