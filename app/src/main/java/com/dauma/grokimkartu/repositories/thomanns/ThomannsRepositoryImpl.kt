package com.dauma.grokimkartu.repositories.thomanns

import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann

class ThomannsRepositoryImpl(
    private val thomannsDao: ThomannsDao
) : ThomannsRepository {
    override fun getThomanns(onComplete: (Boolean, List<Thomann>?, ThomannsError?) -> Unit) {
        thomannsDao.getThomanns() { isSuccessful, thommansDao, e ->
            if (isSuccessful && thommansDao != null) {
                val thomanns = thommansDao.map { td ->
                    Thomann(
                        td.id,
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