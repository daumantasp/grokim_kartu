package com.dauma.grokimkartu.repositories.thomanns

import com.dauma.grokimkartu.data.auth.AuthDao
import com.dauma.grokimkartu.data.auth.entities.AuthUser
import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannDao
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
import com.dauma.grokimkartu.repositories.users.AuthenticationError
import com.dauma.grokimkartu.repositories.users.AuthenticationException
import com.google.firebase.Timestamp

class ThomannsRepositoryImpl(
    private val authDao: AuthDao,
    private val thomannsDao: ThomannsDao
) : ThomannsRepository {
    override fun getThomanns(onComplete: (Boolean, List<Thomann>?, ThomannsError?) -> Unit) {
        thomannsDao.getThomanns() { isSuccessful, thommansDao, e ->
            if (isSuccessful && thommansDao != null) {
                val thomanns = thommansDao.map { td -> toThomann(td)!! }
                onComplete(true, thomanns, null)
            } else {
                onComplete(false, null, ThomannsError(2))
            }
        }
    }

    override fun getThomann(id: String, onComplete: (Thomann?, ThomannsError?) -> Unit) {
        thomannsDao.getThomann(id) { thomannDao, e ->
            if (thomannDao != null) {
                val thomann = toThomann(thomannDao!!)
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
            Timestamp.now()
        )
        thomannsDao.createThomann(thomannDao) { isSuccessful, e ->
            if (isSuccessful) {
                onComplete(isSuccessful, null)
            } else {
                onComplete(isSuccessful, ThomannsError(2))
            }
        }
    }

    private fun toThomann(thomannDao: ThomannDao?) : Thomann? {
        if (thomannDao != null) {
            return Thomann(
                thomannDao.id,
                thomannDao.userId,
                thomannDao.name,
                thomannDao.city,
                thomannDao.isLocked,
                thomannDao.creationDate,
                thomannDao.validUntil
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
        else -> ""
    }

    companion object {
        const val THOMANN_NOT_FOUND = "Thomann was not found!"
        const val SOMETHING_FAILED = "Something failed"
    }
}