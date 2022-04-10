package com.dauma.grokimkartu.viewmodels.main

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.utils.time.TimeUtils
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannUser
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ThomannDetailsViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository,
    private val playersRepository: PlayersRepository,
    private val timeUtils: TimeUtils,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val thomannId = savedStateHandle.get<String>("thomannId")
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _detailsLoaded = MutableLiveData<ThomannDetails>()
    private val _userDetails = MutableLiveData<Event<String>>()
    private val _join = MutableLiveData<Event<String>>()
    private val _edit = MutableLiveData<Event<String>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val detailsLoaded: LiveData<ThomannDetails> = _detailsLoaded
    val userDetails: LiveData<Event<String>> = _userDetails
    val join: LiveData<Event<String>> = _join
    val edit: LiveData<Event<String>> = _edit

    companion object {
        private val TAG = "ThomannDetailsViewModel"
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun joinClicked(amount: Double, onComplete: () -> Unit = {}) {
        thomannsRepository.join(thomannId ?: "", amount) { isSuccessful, e ->
            if (isSuccessful) {
                this.loadDetails()
            }
            onComplete()
        }
    }

    fun leaveClicked() {
        thomannsRepository.leaveThomann(thomannId ?: "") { isSuccessful, e ->
            if (isSuccessful) {
                this.loadDetails()
            }
        }
    }

    fun kickClicked(userId: String) {
        thomannsRepository.kickUserFromThomann(thomannId ?: "", userId) { isSuccessful, e ->
            if (isSuccessful) {
                this.loadDetails()
            }
        }
    }

    fun cancelClicked() {
        thomannsRepository.deleteThomann(thomannId ?: "") { isSuccessful, e ->
            if (isSuccessful) {
                _navigateBack.value = Event("")
            }
        }
    }

    fun lockClicked(lock: Boolean) {
        if (lock == true) {
            thomannsRepository.lockThomann(thomannId ?: "") { isSuccessful, e ->
                if (isSuccessful) {
                    this.loadDetails()
                }
            }
        } else {
            thomannsRepository.unlockThomann(thomannId ?: "") { isSuccessful, e ->
                if (isSuccessful) {
                    this.loadDetails()
                }
            }
        }
    }

    fun userClicked(userId: String) {
        _userDetails.value = Event(userId)
    }

    fun editClicked() {
        _edit.value = Event(thomannId ?: "")
    }

    fun loadDetails() {
        val details = ThomannDetails()
        var isThomannAndProfilePhotoLoaded = false
        var areThomannActionsLoaded = false

        fun notifyThatDetailsHaveBeenLoadedIfNeeded() {
            if (isThomannAndProfilePhotoLoaded == true && areThomannActionsLoaded == true) {
                _detailsLoaded.value = details
            }
        }

        thomannsRepository.getThomann(thomannId ?: "") { thomann, thomannsError ->
            if (thomann != null) {
                val currentTime = Date()
                details.name = thomann.name ?: ""
                details.city = thomann.city ?: ""
                details.creationDate = this.timeUtils.format(thomann.creationDate?.toDate() ?: currentTime)
                details.validUntilDate = this.timeUtils.format(thomann.validUntil?.toDate() ?: currentTime)
                details.isLocked = thomann.isLocked ?: false
                details.users = thomann.users ?: listOf()

                this.playersRepository.getPlayerPhoto(thomann.userId ?: "") { playerPhoto, playerError ->
                    if (playerPhoto != null) {
                        details.photo = playerPhoto
                    }
                    isThomannAndProfilePhotoLoaded = true
                    notifyThatDetailsHaveBeenLoadedIfNeeded()
                }
            }
        }

        this.thomannsRepository.getThomannActions(thomannId ?: "") { thomannActions, e ->
            if (thomannActions != null) {
                if (thomannActions.isJoinable == true) {
                    details.onJoinButtonClick = {
                        this._join.value = Event("")
                    }
                }
                if (thomannActions.isUpdatable == true) {
                    details.onCancelButtonClick = { this.cancelClicked() }
                    details.onLockButtonClick = { this.lockClicked(it) }
                    details.onEditButtonClick = { this.editClicked() }
                }
            }
            details.isJoinable = thomannActions?.isJoinable ?: false
            details.isCancelable = thomannActions?.isUpdatable ?: false
            details.isLockable = thomannActions?.isUpdatable ?: false
            details.isEditable = thomannActions?.isUpdatable ?: false
            areThomannActionsLoaded = true
            notifyThatDetailsHaveBeenLoadedIfNeeded()
        }
    }
}

class ThomannDetails {
    var name: String = ""
    var city: String = ""
    var creationDate: String = ""
    var validUntilDate: String = ""
    var photo: Bitmap? = null
    var isLocked: Boolean = false
    var users: List<ThomannUser> = listOf()
    var isJoinable: Boolean = false
    var onJoinButtonClick: () -> Unit = {}
    var isCancelable: Boolean = false
    var onCancelButtonClick: () -> Unit = {}
    var isLockable: Boolean = false
    var onLockButtonClick: (Boolean) -> Unit = {}
    var isEditable: Boolean = false
    var onEditButtonClick: () -> Unit = {}
}