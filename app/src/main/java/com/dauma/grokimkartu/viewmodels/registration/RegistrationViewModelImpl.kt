package com.dauma.grokimkartu.viewmodels.registration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.models.forms.RegistrationForm
import com.dauma.grokimkartu.models.users.RegistrationUser
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
    val emailVerificationSent: LiveData<Event<Any>> = _emailVerificationSent

    companion object {
        private val TAG = "RegistrationViewModel"
    }

    override fun createUser(name: String, email: String, password: String) {
        val registrationUser = RegistrationUser(name, email, password)
        try {
            usersRepository.registerUser(registrationUser) { isSuccessful, createdUser ->
                if (isSuccessful) {
                    this.usersRepository.sendEmailVerification()
                    this.usersRepository.logOut()
                    _emailVerificationSent.value = Event(isSuccessful)
                } else {
                    Log.d(TAG, "Registration was unsuccessful")
                }
            }
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "Registration was unsuccessful")
        }
    }

    fun getRegistrationForm() : RegistrationForm {
        return registrationForm
    }
}