package com.dauma.grokimkartu.viewmodels.main

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannUser
import com.dauma.grokimkartu.repositories.thomanns.entities.UpdateThomann
import dagger.hilt.android.lifecycle.HiltViewModel
import java.sql.Timestamp
import javax.inject.Inject

@HiltViewModel
class ThomannDetailsViewModel @Inject constructor(
    private val thomannsRepository: ThomannsRepository,
    private val playersRepository: PlayersRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val thomannId = savedStateHandle.get<Int>("thomannId")
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _detailsLoaded = MutableLiveData<ThomannDetails>()
    private val _message = MutableLiveData<Event<Int>>() // TODO: refactor
    private val _join = MutableLiveData<Event<Int>>()
    private val _quit = MutableLiveData<Event<Int>>()
    private val _edit = MutableLiveData<Event<Int>>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val detailsLoaded: LiveData<ThomannDetails> = _detailsLoaded
    val join: LiveData<Event<Int>> = _join
    val quit: LiveData<Event<Int>> = _quit
    val edit: LiveData<Event<Int>> = _edit
    val message: LiveData<Event<Int>> = _message

    companion object {
        private val TAG = "ThomannDetailsViewModel"
    }

    fun backClicked() {
        _navigation.value = Event(NavigationCommand.Back)
    }

    fun joinClicked(amount: Int, onComplete: () -> Unit = {}) {
        if (thomannId != null) {
            thomannsRepository.join(thomannId, amount.toDouble()) { thomannDetails, thomannsErrors ->
                if (thomannDetails != null) {
                    this.loadDetails()
                }
                onComplete()
            }
        }
    }

    fun quitClicked() {
        if (thomannId != null) {
            thomannsRepository.quit(thomannId) { thomannDetails, thomannsErrors ->
                if (thomannDetails != null) {
                    this.loadDetails()
                }
            }
        }
    }

    fun kickClicked(userId: Int) {
        if (thomannId != null) {
            thomannsRepository.kick(thomannId, userId) { thomannDetails, thomannsErrors ->
                if (thomannDetails != null) {
                    this.loadDetails()
                }
            }
        }
    }

    fun cancelClicked() {
        if (thomannId != null) {
            thomannsRepository.delete(thomannId) { thomannsErrors ->
                if (thomannsErrors == null) {
                    _navigation.value = Event(NavigationCommand.Back)
                }
            }
        }
    }

    fun lockClicked(isLocked: Boolean) {
        if (thomannId != null) {
            val updateThomann = UpdateThomann(
                isLocked = isLocked,
                cityId = null,
                validUntil = null
            )
            thomannsRepository.update(thomannId, updateThomann) { thomannDetails, thomannsErrors ->
                if (thomannDetails != null) {
                    this.loadDetails()
                }
            }
        }
    }

    fun editClicked() {
        if (thomannId != null) {
            _edit.value = Event(thomannId)
        }
    }

    fun postMessageClicked() {
        if (thomannId != null) {
            _message.value = Event(thomannId)
        }
    }

    fun loadDetails() {
        if (thomannId == null) {
            return
        }

        var details: ThomannDetails? = null
        thomannsRepository.thomannDetails(thomannId) { thomannDetails, thomannsErrors ->
            if (thomannDetails != null) {
                val isJoinable = thomannDetails.actions?.contains("JOIN")
                val isQuitable = thomannDetails.actions?.contains("QUIT")
                details = ThomannDetails(
                    user = thomannDetails.user?.name,
                    city = thomannDetails.city?.name,
                    isOwner = thomannDetails.isOwner,
                    isLocked = thomannDetails.isLocked,
                    createdAt = thomannDetails.createdAt,
                    validUntil = thomannDetails.validUntil,
                    users = thomannDetails.users,
                    totalAmount = thomannDetails.totalAmount,
                    isJoinable = isJoinable,
                    isQuitable = isQuitable
                )

                if (isJoinable == true) {
                    details?.onJoinClicked = {
                        if (thomannDetails.id != null) {
                            this._join.value = Event(thomannDetails.id!!)
                        }
                    }
                }

                if (isQuitable == true) {
                    details?.onQuitClicked = {
                        if (thomannDetails.id != null) {
                            this._quit.value = Event(thomannDetails.id!!)
                        }
                    }
                }

                if (thomannDetails.isOwner == true) {
                    details?.onEditClicked = {
                        this.editClicked()
                    }
                    details?.onCancelClicked = {
                        this.cancelClicked()
                    }
                    details?.onLockClicked = { isLocked ->
                        this.lockClicked(isLocked)
                    }
                    details?.onPostMessageClicked = {
                        this.postMessageClicked()
                    }
                } else {
                    for (user in thomannDetails.users ?: listOf()) {
                        if (user.isCurrentUser == true) {
                            details?.onPostMessageClicked = {
                                this.postMessageClicked()
                            }
                            break
                        }
                    }
                }

                if (thomannDetails.user?.id != null) {
                    this.playersRepository.playerPhoto(thomannDetails.user!!.id!!) { photo, playersErrors ->
                        details?.photo = photo
                        _detailsLoaded.value = details!!
                    }
                } else {
                    _detailsLoaded.value = details!!
                }
            }
        }
    }
}

class ThomannDetails(
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