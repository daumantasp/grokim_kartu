package com.dauma.grokimkartu.viewmodels.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.Event
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.dauma.grokimkartu.repositories.users.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModelImpl @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel(), ProfileViewModel {
    private val _navigateToLogin = MutableLiveData<Event<Any>>()
    val navigateToLogin: LiveData<Event<Any>> = _navigateToLogin

    companion object {
        private val TAG = "ProfileViewModelImpl"
    }

    override fun logout() {
        try {
            usersRepository.logOut()
            _navigateToLogin.value = Event(R.id.action_profileFragment_to_loginFragment)
        } catch (e: AuthenticationException) {
            Log.d(TAG, e.message ?: "Login was unsuccessful")
        }
    }
}