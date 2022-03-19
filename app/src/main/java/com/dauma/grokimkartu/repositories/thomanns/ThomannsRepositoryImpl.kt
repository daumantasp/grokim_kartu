package com.dauma.grokimkartu.repositories.thomanns

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.auth.AuthDao
import com.dauma.grokimkartu.data.auth.entities.AuthUser
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannUserDao
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
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
            Timestamp.now(),
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

    // TODO: consider getting thomann from DB instead of passing it as an parameter
    // Also should be implemented at DB level
    override fun isJoinPossible(thomann: Thomann): Boolean {
        if (isUserLoggedIn() == true) {
            val userId = authDao.getUserId()
            val isCreator = thomann.userId == userId
            return thomann.isLocked == false && isCreator == false && hasAlreadyJoined(thomann) == false
        } else {
            val error = ThomannsError(3)
            throw ThomannsException(error)
        }
    }

    // TODO: consider getting thomann from DB instead of passing it as an parameter
    override fun hasAlreadyJoined(thomann: Thomann): Boolean {
        if (isUserLoggedIn() == true) {
            val userId = authDao.getUserId()
            var hasAlreadyJoined = false
            if (thomann.users != null) {
                for (user in thomann.users!!) {
                    if (user.userId == userId) {
                        hasAlreadyJoined = true
                        break
                    }
                }
            }
            return hasAlreadyJoined
        } else {
            val error = ThomannsError(3)
            throw ThomannsException(error)
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
            val thomannUser = ThomannUser(authUser.id, authUser.name, amount)
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
                thomannDao.users?.map { tud -> toThomannUser(tud)!! },
                thomannPlayerIcon
            )
        }
        return null
    }

    private fun toThomannUser(thomannUserDao: ThomannUserDao?) : ThomannUser? {
        if (thomannUserDao != null) {
            return ThomannUser(
                thomannUserDao.userId,
                thomannUserDao.userName,
                thomannUserDao.amount
            )
        }
        return null
    }

    private fun toThomannUserDao(thomannUser: ThomannUser?) : ThomannUserDao? {
        if (thomannUser != null) {
            return ThomannUserDao(
                thomannUser.userId,
                thomannUser.userName,
                thomannUser.amount
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