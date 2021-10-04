package com.dauma.grokimkartu.repositories.users

import com.dauma.grokimkartu.models.users.RegistrationUser
import com.dauma.grokimkartu.models.users.User

interface UsersRepository {
    fun registerUser(user: RegistrationUser, onComplete: (Boolean, User?) -> Unit)
}