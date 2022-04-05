package com.dauma.grokimkartu.repositories.thomanns

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.auth.AuthDao
import com.dauma.grokimkartu.data.auth.entities.AuthUser
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannActionsDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannUserDao
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannActions
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannPlayerIcon
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannUser
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.google.firebase.Timestamp

class ThomannsRepositoryImpl(
    private val authDao: AuthDao,
    private val thomannsDao: ThomannsDao,
    private val playersDao: PlayersDao
) : ThomannsRepository {
    override fun getThomanns(onComplete: (Boolean, List<Thomann>?, ThomannsError?) -> Unit) {
        thomannsDao.getThomanns() { isSuccessful, thommansDao, e ->
            if (isSuccessful && thommansDao != null) {
                val thomanns = thommansDao.map { td ->
                    val loader = { onComplete: (Bitmap?, ThomannsError?) -> Unit ->
                        this.getPlayerIcon(td.userId ?: "", onComplete)
                    }
                    val thomannPlayerIcon = ThomannPlayerIcon(loader)
                    toThomann(td, thomannPlayerIcon)!!
                }
                onComplete(true, thomanns, null)
            } else {
                onComplete(false, null, ThomannsError(2))
            }
        }
    }

    override fun getThomann(id: String, onComplete: (Thomann?, ThomannsError?) -> Unit) {
        thomannsDao.getThomann(id) { thomannDao, e ->
            if (thomannDao != null) {
                val loader = { onComplete: (Bitmap?, ThomannsError?) -> Unit ->
                    this.getPlayerIcon(thomannDao.userId ?: "", onComplete)
                }
                val thomannPlayerIcon = ThomannPlayerIcon(loader)
                val thomann = toThomann(thomannDao!!, thomannPlayerIcon)
                onComplete(thomann, null)
            } else {
                onComplete(null, ThomannsError(2))
            }
        }
    }

    override fun getThomannActions(
        id: String,
        onComplete: (ThomannActions?, ThomannsError?) -> Unit
    ) {
        if (isUserLoggedIn() == true) {
            val userId = authDao.getUserId()
            thomannsDao.getThomannActions(id, userId ?: "") { thomannActionsDao, e ->
                if (thomannActionsDao != null) {
                    val thomannActions = toThomannActions(thomannActionsDao)
                    onComplete(thomannActions, null)
                } else {
                    val error = ThomannsError(2)
                    onComplete(null, error)
                }
            }
        } else {
            val error = ThomannsError(2)
            onComplete(null, error)
        }
    }

    override fun saveThomann(
        thomann: Thomann,
        onComplete: (Boolean, ThomannsError?) -> Unit
    ) {
        val userDataProfiles = authDao.getUserDataProfiles()
        var authUser: AuthUser? = null
        if (userDataProfiles.count() > 0) {
            authUser = userDataProfiles[0]
        } else {
            val error = AuthenticationError(5)
            throw AuthenticationException(error)
        }
        val thomannDao = ThomannDao(
            null,
            authUser.id,
            authUser.name,
            thomann.city,
            false,
            null,
            thomann.validUntil,
            listOf()
        )
        thomannsDao.createThomann(thomannDao) { isSuccessful, e ->
            if (isSuccessful) {
                onComplete(isSuccessful, null)
            } else {
                onComplete(isSuccessful, ThomannsError(2))
            }
        }
    }

    private fun getPlayerIcon(userId: String, onComplete: (Bitmap?, ThomannsError?) -> Unit) {
        playersDao.getPlayerIcon(userId) { playerIcon, e ->
            if (playerIcon != null) {
                onComplete(playerIcon, null)
            } else {
                onComplete(null, ThomannsError(2))
            }
        }
    }

    override fun join(id: String, amount: Double, onComplete: (Boolean, ThomannsError?) -> Unit) {
        if (isUserLoggedIn() == true) {
            val userDataProfiles = authDao.getUserDataProfiles()
            var authUser: AuthUser? = null
            if (userDataProfiles.count() > 0) {
                authUser = userDataProfiles[0]
            } else {
                val error = AuthenticationError(5)
                throw AuthenticationException(error)
            }
            val thomannUser = ThomannUser(
                userId = authUser.id,
                userName = authUser.name,
                thomannId = id,
                amount = amount,
                isCurrentUser = null,
                isUserCreator = null,
                icon = null,
                joinDate = null
            )
            val thomannUserDao = toThomannUserDao(thomannUser)!!
            thomannsDao.joinThomann(id, thomannUserDao) { isSuccessful, e ->
                if (isSuccessful) {
                    onComplete(true, null)
                } else {
                    val error = ThomannsError(2)
                    onComplete(false, error)
                }
            }
        } else {
            val error = ThomannsError(3)
            throw ThomannsException(error)
        }
    }

    override fun deleteThomann(id: String, onComplete: (Boolean, ThomannsError?) -> Unit) {
        if (isUserLoggedIn() == true) {
            val userId = authDao.getUserId()
            thomannsDao.deleteThomann(id, userId ?: "") { isSuccessful, e ->
                if (isSuccessful) {
                    onComplete(true, null)
                } else {
                    val error = ThomannsError(2)
                    onComplete(false, error)
                }
            }
        } else {
            val error = ThomannsError(2)
            onComplete(false, error)
        }
    }

    override fun leaveThomann(
        id: String,
        onComplete: (Boolean, ThomannsError?) -> Unit
    ) {
        if (isUserLoggedIn() == true) {
            val userId = authDao.getUserId() ?: ""
            thomannsDao.leaveThomann(id, userId) { isSuccessful, e ->
                if (isSuccessful) {
                    onComplete(true, null)
                } else {
                    val error = ThomannsError(2)
                    onComplete(false, error)
                }
            }
        } else {
            val error = ThomannsError(3)
            throw ThomannsException(error)
        }
    }

    override fun lockThomann(
        thomannId: String,
        onComplete: (Boolean, ThomannsError?) -> Unit
    ) {
        if (isUserLoggedIn() == true) {
            val userId = authDao.getUserId() ?: ""
            thomannsDao.lockThomann(thomannId, userId) { isSuccessful, e ->
                if (isSuccessful) {
                    onComplete(true, null)
                } else {
                    val error = ThomannsError(2)
                    onComplete(false, error)
                }
            }
        } else {
            val error = ThomannsError(3)
            throw ThomannsException(error)
        }
    }

    override fun unlockThomann(
        thomannId: String,
        onComplete: (Boolean, ThomannsError?) -> Unit
    ) {
        if (isUserLoggedIn() == true) {
            val userId = authDao.getUserId() ?: ""
            thomannsDao.unlockThomann(thomannId, userId) { isSuccessful, e ->
                if (isSuccessful) {
                    onComplete(true, null)
                } else {
                    val error = ThomannsError(2)
                    onComplete(false, error)
                }
            }
        } else {
            val error = ThomannsError(3)
            throw ThomannsException(error)
        }
    }

    override fun kickUserFromThomann(
        thomannId: String,
        userToKickId: String,
        onComplete: (Boolean, ThomannsError?) -> Unit
    ) {
        if (isUserLoggedIn() == true) {
            val userId = authDao.getUserId() ?: ""
            thomannsDao.kickUserFromThomann(thomannId, userId, userToKickId) { isSuccessful, e ->
                if (isSuccessful) {
                    onComplete(true, null)
                } else {
                    val error = ThomannsError(2)
                    onComplete(false, error)
                }
            }
        } else {
            val error = ThomannsError(3)
            throw ThomannsException(error)
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return authDao.getUserId() != null
    }

    private fun toThomann(thomannDao: ThomannDao?, thomannPlayerIcon: ThomannPlayerIcon?) : Thomann? {
        if (thomannDao != null) {
            return Thomann(
                thomannDao.id,
                thomannDao.userId,
                thomannDao.name,
                thomannDao.city,
                thomannDao.isLocked,
                thomannDao.creationDate,
                thomannDao.validUntil,
                thomannDao.users?.map { tud ->
                    val loader = { onComplete: (Bitmap?, ThomannsError?) -> Unit ->
                        this.getPlayerIcon(tud.userId ?: "", onComplete)
                    }
                    val thomannPlayerUserIcon = ThomannPlayerIcon(loader)
                    toThomannUser(tud, thomannDao.userId ?: "", thomannPlayerUserIcon)!!
                },
                thomannPlayerIcon
            )
        }
        return null
    }

    private fun toThomannUser(thomannUserDao: ThomannUserDao?, creatorUserId: String, thomannPlayerIcon: ThomannPlayerIcon?) : ThomannUser? {
        if (thomannUserDao != null) {
            var isCurrentUser = false
            var isCurrentUserCreator = false
            if (thomannUserDao.userId != null) {
                isCurrentUser = thomannUserDao.userId == authDao.getUserId()
                isCurrentUserCreator = creatorUserId == authDao.getUserId()
            }
            return ThomannUser(
                thomannUserDao.userId,
                thomannUserDao.userName,
                thomannUserDao.thomannId,
                thomannUserDao.amount,
                isCurrentUser,
                isCurrentUserCreator,
                thomannPlayerIcon,
                thomannUserDao.joinDate
            )
        }
        return null
    }

    private fun toThomannUserDao(thomannUser: ThomannUser?) : ThomannUserDao? {
        if (thomannUser != null) {
            return ThomannUserDao(
                thomannUser.userId,
                thomannUser.userName,
                thomannUser.thomannId,
                thomannUser.amount,
                thomannUser.joinDate
            )
        }
        return null
    }

    private fun toThomannActions(thomannActionsDao: ThomannActionsDao?) : ThomannActions? {
        if (thomannActionsDao != null) {
            return ThomannActions(
                thomannActionsDao.thomannId,
                thomannActionsDao.isAccessible,
                thomannActionsDao.isJoinable,
                thomannActionsDao.isLeavable,
                thomannActionsDao.isUpdatable
            )
        }
        return null
    }
}

class ThomannsException(error: ThomannsError)
    : Exception(error.message) {}

class ThomannsError(val code: Int) {
    val message: String = when(code) {
        1 -> THOMANN_NOT_FOUND
        2 -> SOMETHING_FAILED
        3 -> USER_NOT_LOGGED_IN
        else -> ""
    }

    companion object {
        const val THOMANN_NOT_FOUND = "Thomann was not found!"
        const val SOMETHING_FAILED = "Something failed"
        const val USER_NOT_LOGGED_IN = "User is not logged in!"
    }
}