package com.dauma.grokimkartu.viewmodels.main

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannUser
import com.dauma.grokimkartu.repositories.thomanns.entities.UpdateThomann
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.sql.Timestamp
import javax.inject.Inject

// TODO: UGLY CODE, REFACTOR

data class ThomannDetailsUiState(
    val details: ThomannDetails? = null,
    val isJoinStarted: Boolean = false,
    val isEditStarted: Boolean = false,
    val isPostMessageStarted: Boolean = false,
    val close: Boolean = false
)

@HiltViewModel
class ThomannDetailsViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository,
    private val playersRepository: PlayersRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val thomannId = savedStateHandle.get<Int>("thomannId")

    private val _uiState = MutableStateFlow(ThomannDetailsUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val TAG = "ThomannDetailsViewModel"
    }

    fun back() = _uiState.update { it.copy(close = true) }

    fun cancelDialogClicked() {
        if (_uiState.value.isJoinStarted) {
            _uiState.update { it.copy(isJoinStarted = false) }
        }
    }

    fun joinClicked(amount: Int, onComplete: () -> Unit = {}) {
        if (thomannId == null)
            return

        viewModelScope.launch {
            thomannsRepository.join(thomannId, amount.toDouble())
            loadDetails()
            onComplete()
        }

        _uiState.update { it.copy(isJoinStarted = false) }
    }

    fun quitClicked() {
        if (thomannId == null)
            return

        viewModelScope.launch {
            thomannsRepository.quit(thomannId)
            loadDetails()
        }
    }

    fun kickClicked(userId: Int) {
        if (thomannId == null)
            return

        viewModelScope.launch {
            thomannsRepository.kick(thomannId, userId)
            loadDetails()
        }
    }

    fun cancelClicked() {
        if (thomannId == null)
            return

        viewModelScope.launch {
            val deleteResponse = thomannsRepository.delete(thomannId)
            if (deleteResponse.error == null)
                _uiState.update { it.copy(close = true) }
        }
    }

    fun lockClicked(isLocked: Boolean) {
        if (thomannId == null)
            return

        viewModelScope.launch {
            thomannsRepository.update(
                thomannId,
                UpdateThomann(isLocked = isLocked, cityId = null, validUntil = null)
            )
            loadDetails()
        }
    }

    fun loadDetails() {
        if (thomannId == null)
            return

        var details: ThomannDetails? = null
        viewModelScope.launch {
            val detailsResponse = thomannsRepository.thomannDetails(thomannId)
            detailsResponse.data?.let {
                val isJoinable = it.actions?.contains("JOIN")
                val isQuitable = it.actions?.contains("QUIT")
                details = ThomannDetails(
                    id = thomannId,
                    user = it.user?.name,
                    city = it.city?.name,
                    isOwner = it.isOwner,
                    isLocked = it.isLocked,
                    createdAt = it.createdAt,
                    validUntil = it.validUntil,
                    users = it.users,
                    totalAmount = it.totalAmount,
                    isJoinable = isJoinable,
                    isQuitable = isQuitable
                )

                if (isJoinable == true) {
                    details?.onJoinClicked = {
                        _uiState.update { it.copy(isJoinStarted = true) }
                    }
                }

                if (it.isOwner == true) {
                    details?.onEditClicked = {
                        _uiState.update { it.copy(isEditStarted = true) }
                    }
                    details?.onCancelClicked = {
                        cancelClicked()
                    }
                    details?.onLockClicked = { isLocked ->
                        lockClicked(isLocked)
                    }
                    details?.onPostMessageClicked = {
                        _uiState.update { it.copy(isPostMessageStarted = true) }
                    }
                } else {
                    // CAN NOT REMEMBER WHY THIS IS NEEDED
                    for (user in details?.users ?: listOf()) {
                        if (user.isCurrentUser == true) {
                            details?.onPostMessageClicked = {
                                _uiState.update { it.copy(isPostMessageStarted = true) }
                            }
                            break
                        }
                    }
                }

                it.user?.id?.let { userId ->
                    val photoResponse = playersRepository.playerPhoto(userId)
                    photoResponse.data?.let { bitmap ->
                        details?.photo = bitmap
                    }
                }

                _uiState.update { it.copy(details = details) }
            }
        }
    }
}

class ThomannDetails(
    val id: Int?,
    val user: String?,
    val city: String?,
    val isOwner: Boolean?,
    val isLocked: Boolean?,
    val createdAt: Timestamp?,
    val validUntil: Timestamp?,
    val users: List<ThomannUser>?,
    val totalAmount: String?,
    val isJoinable: Boolean?,
    val isQuitable: Boolean?
) {
    var photo: Bitmap? = null
    var onJoinClicked: () -> Unit = {}
    var onQuitClicked: () -> Unit = {}
    var onLockClicked: (Boolean) -> Unit = {}
    var onEditClicked: () -> Unit = {}
    var onCancelClicked: () -> Unit = {}
    var onPostMessageClicked: () -> Unit = {}
}