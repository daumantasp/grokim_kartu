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
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val detailsLoaded: LiveData<ThomannDetails> = _detailsLoaded
    val userDetails: LiveData<Event<String>> = _userDetails

    companion object {
        private val TAG = "ThomannDetailsViewModel"
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun joinClicked() {
        thomannsRepository.join(thomannId ?: "", 20.0) { isSuccessful, e ->
            if (isSuccessful) {
                this.loadDetails()
            }
        }
    }

    fun leaveClicked() {
        thomannsRepository.leaveThomann(thomannId ?: "") { isSuccessful, e ->
            if (isSuccessful) {
                this.loadDetails()
            }
        }
    }

    fun userClicked(userId: String) {
        _userDetails.value = Event(userId)
    }

    fun loadDetails() {
        val details = ThomannDetails()
        var isThomannAndProfilePhotoLoaded = false
        var isThomannJoinableLoaded = false

        fun notifyThatDetailsHaveBeenLoadedIfNeeded() {
            if (isThomannAndProfilePhotoLoaded == true && isThomannJoinableLoaded == true) {
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

        this.thomannsRepository.isJoinable(thomannId ?: "") { isSuccessful, isJoinable, e ->
            if (isSuccessful == true && isJoinable == true) {
                details.onJoinButtonClick = { this.joinClicked() }
            }
            details.isJoinable = isJoinable ?: false
            isThomannJoinableLoaded = true
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
    var onLockButtonClick: () -> Unit = {}
}