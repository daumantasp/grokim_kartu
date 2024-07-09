package com.dauma.grokimkartu.viewmodels.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.models.forms.DeleteUserForm
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeleteUserUiState(
    val isDeleteStarted: Boolean = false,
    val isDeleteSuccessful: Boolean = false,
    val passwordError: Int = -1,
    val close: Boolean = false
)

@HiltViewModel
class DeleteUserViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val deleteUserForm: DeleteUserForm
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeleteUserUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val TAG = "DeleteUserViewModel"
    }

    fun getDeleteUserForm() : DeleteUserForm {
        return deleteUserForm
    }

    fun back() = _uiState.update { it.copy(close = true, isDeleteSuccessful = false) }

    fun deleteUserClicked() {
        if (!deleteUserForm.isPasswordValid())
            return

        try {
            _uiState.update { it.copy(isDeleteStarted = true) }
            viewModelScope.launch {
                val deleteResponse = authRepository.delete()
                val isSuccessful = deleteResponse.data
                if (isSuccessful) {
                    _uiState.update { it.copy(isDeleteStarted = false, isDeleteSuccessful = true) }
                } else {
                    deleteResponse.error?.let {
                        handleAuthenticationError(it)
                    }
                }
            }
        } catch (e: AuthenticationException) {
            _uiState.update { it.copy(isDeleteStarted = false, isDeleteSuccessful = false) }
            Log.d(TAG, e.message ?: "User delete was unsuccessful")
        }
    }

    private fun handleAuthenticationError(error: AuthenticationErrors) {
        when(error) {
//            AuthenticationErrors.INVALID_PASSWORD -> {
//                _passwordError.value = R.string.login_invalid_password_error
//            }
            else -> clearAuthenticationErrors()
        }
    }

    private fun clearAuthenticationErrors() {
        _uiState.update { it.copy(isDeleteStarted = false, passwordError = -1, isDeleteSuccessful = false) }
    }
}