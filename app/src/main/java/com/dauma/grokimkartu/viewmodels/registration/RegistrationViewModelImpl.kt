package com.dauma.grokimkartu.viewmodels.registration

import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.models.forms.RegistrationForm
import com.dauma.grokimkartu.models.users.RegistrationUser
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModelImpl @Inject constructor(
    private val usersRepository: UsersRepository,
    private val registrationForm: RegistrationForm,
) : ViewModel(), RegistrationViewModel {

    override fun createUser(name: String, email: String, password: String) {
        val registrationUser = RegistrationUser(name, email, password)
        usersRepository.registerUser(registrationUser) { isSuccessful, createdUser ->
            if (isSuccessful) {
            } else {
            }
        }
    }

    fun getRegistrationForm() : RegistrationForm {
        return registrationForm
    }
}