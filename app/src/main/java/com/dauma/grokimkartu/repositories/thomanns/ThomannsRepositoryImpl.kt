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
                val thomanns = thommansDao.map { td ->
                    Thomann(
                        td.id,
                        td.userId,
                        td.name,
                        td.city,
                        td.isLocked,
                        td.creationDate,
                        td.validUntil
                    )
                }
                onComplete(true, thomanns, null)
            } else {
                onComplete(false, null, ThomannsError(2))
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