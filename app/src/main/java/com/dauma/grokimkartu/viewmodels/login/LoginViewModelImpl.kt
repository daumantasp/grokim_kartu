package com.dauma.grokimkartu.viewmodels.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.Event
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
    private val _navigateToPlayers = MutableLiveData<Event<Any>>()
    val navigateToPlayers: LiveData<Event<Any>> = _navigateToPlayers

    override fun loginUser(email: String, password: String) {
        val loginUser = LoginUser(email, password)
        usersRepository.loginUser(loginUser) { isSuccessful, e ->
            if (isSuccessful) {
                _navigateToPlayers.value = Event(R.id.action_loginFragment_to_playersFragment)
            }
        }
    }

    fun getLoginForm() : LoginForm {
        return loginForm
    }
}