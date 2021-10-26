package com.dauma.grokimkartu.viewmodels.registration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.models.forms.RegistrationForm
import com.dauma.grokimkartu.models.users.RegistrationUser
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModelImpl @Inject constructor(
    private val usersRepository: UsersRepository,
    private val registrationForm: RegistrationForm,
) : ViewModel(), RegistrationViewModel {
    private val _emailVerificationSent = MutableLiveData<Event<Any>>()
    private val _emailError = MutableLiveData<Int>()
    private val _passwordError = MutableLiveData<Int>()
    val emailVerificationSent: LiveData<Event<Any>> = _emailVerificationSent
    val emailError: LiveData<Int> = _emailError
    val passwordError: LiveData<Int> = _passwordError

    companion object {
        private val TAG = "RegistrationViewModel"
    }

    override fun createUser(name: String, email: String, password: String) {
        val registrationUser = RegistrationUser(name, email, password)
        try {
            usersRepository.registerUser(registrationUser) { isSuccessful, error ->
                if (isSuccessful) {
                    usersRepository.sendEmailVerification()
                    usersRepository.logOut()
                    clearAuthenticationErrors()
                    _emailVerificationSent.value = Event(isSuccessful)
                } else {
                    Log.d(TAG, error?.message ?: "Registration was unsuccessful")
                    if (error != null) {
                        handleAuthenticationError(error)
                    }
                }
            }
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "Registration was unsuccessful")
        }
    }

    fun getRegistrationForm() : RegistrationForm {
        return registrationForm
    }

    private fun handleAuthenticationError(error: AuthenticationError) {
        when(error.message) {
            AuthenticationError.EMAIL_ALREADY_REGISTERED -> {
                _emailError.value = R.string.registration_email_already_exists_error
                _passwordError.value = -1
            }
            AuthenticationError.EMAIL_INCORRECT_FORMAT -> {
                _emailError.value = R.string.registration_email_incorrect_format_error
                _passwordError.value = -1
            }
            AuthenticationError.PASSWORD_TOO_WEAK -> {
                _emailError.value = -1
                _passwordError.value = R.string.registration_email_password_too_weak_error
            }
            else -> clearAuthenticationErrors()
        }
    }

    private fun clearAuthenticationErrors() {
        _emailError.value = -1
        _passwordError.value = -1
    }
}