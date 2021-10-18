package com.dauma.grokimkartu.viewmodels.login

import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.models.forms.LoginForm
import com.dauma.grokimkartu.models.users.LoginUser
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModelImpl @Inject constructor(
    private val usersRepository: UsersRepository,
    private val loginForm: LoginForm
) : ViewModel(), LoginViewModel {
    override fun loginUser(email: String, password: String) {
        val loginUser = LoginUser(email, password)
        usersRepository.loginUser(loginUser) { isSuccessful ->
            if (isSuccessful) {
                // TODO navigate
            }
        }
    }

    fun getLoginForm() : LoginForm {
        return loginForm
    }
}