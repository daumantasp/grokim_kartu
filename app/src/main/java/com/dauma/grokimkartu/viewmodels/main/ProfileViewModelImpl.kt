package com.dauma.grokimkartu.viewmodels.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.models.forms.ProfileForm
import com.dauma.grokimkartu.models.users.LoginUser
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModelImpl @Inject constructor(
    private val usersRepository: UsersRepository,
    private val profileForm: ProfileForm
) : ViewModel() {
    private val _navigateToLogin = MutableLiveData<Event<Any>>()
    val navigateToLogin: LiveData<Event<Any>> = _navigateToLogin

    private val _navigateToPasswordChange = MutableLiveData<Event<Any>>()
    val navigateToPasswordChange: LiveData<Event<Any>> = _navigateToPasswordChange

    private val _passwordError = MutableLiveData<Int>()
    val passwordError: LiveData<Int> = _passwordError

    companion object {
        private val TAG = "ProfileViewModelImpl"
    }

    fun getProfileForm() : ProfileForm {
        return profileForm
    }

    fun deleteUser(password: String) {
        try {
            val email = usersRepository.getAuthenticatedUserData().email
            if (email != null) {
                val loginUser = LoginUser(email, password)
                usersRepository.reauthenticateUser(loginUser) { isSuccessful, error ->
                    if (isSuccessful) {
                        usersRepository.deleteUser() { isSuccessful, error ->
                            if (isSuccessful) {
                                _navigateToLogin.value = Event(R.id.action_profileFragment_to_loginFragment)
                            }
                        }
                    } else {
                        Log.d(TAG, error?.message ?: "Reauthentication was unsuccessful")
                        if (error != null) {
                            handleAuthenticationError(error)
                        }
                    }
                }
            }
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "User delete was unsuccessful")
        }
    }

    fun logout() {
        try {
            usersRepository.logOut()
            _navigateToLogin.value = Event(R.id.action_profileFragment_to_loginFragment)
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "Login was unsuccessful")
        }
    }

    fun passwordChangeClicked() {
        _navigateToPasswordChange.value = Event(R.id.action_profileFragment_to_passwordChangeFragment)
    }

    private fun handleAuthenticationError(error: AuthenticationError) {
        when(error.message) {
            AuthenticationError.INVALID_PASSWORD -> {
                _passwordError.value = R.string.login_invalid_password_error
            }
            else -> clearAuthenticationErrors()
        }
    }

    private fun clearAuthenticationErrors() {
        _passwordError.value = -1
    }
}