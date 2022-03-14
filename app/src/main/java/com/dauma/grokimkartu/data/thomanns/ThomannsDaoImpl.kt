package com.dauma.grokimkartu.data.thomanns

import com.dauma.grokimkartu.data.firestore.Firestore
import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomannUser
import com.dauma.grokimkartu.data.thomanns.entities.ThomannDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannUserDao

class ThomannsDaoImpl(
    private val firebase: Firestore
) : ThomannsDao {
    override fun createThomann(thomann: ThomannDao, onComplete: (Boolean, Exception?) -> Unit) {
        val firestoreThomann = toFirestoreThomann(thomann)
        if (firestoreThomann != null) {
            firebase.createThomann(firestoreThomann!!, onComplete)
        } else {
            onComplete(false, Exception())
        }
    }

    override fun updateThomann(thomann: ThomannDao, onComplete: (Boolean, Exception?) -> Unit) {
        val firestoreThomann = toFirestoreThomann(thomann)
        if (firestoreThomann != null) {
            firebase.updateThomann(firestoreThomann!!, onComplete)
        } else {
            onComplete(false, Exception())
        }
    }

    override fun deleteThomann(thomannId: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebase.deleteThomann(thomannId, onComplete)
    }

    override fun getThomanns(onComplete: (Boolean, List<ThomannDao>?, Exception?) -> Unit) {
        firebase.getThomanns { isSuccessful, firestoreThomannsList, e ->
            val thomannsDaoList = firestoreThomannsList?.map { ft -> toThomannDao(ft)!! }
            onComplete(isSuccessful, thomannsDaoList, e)
        }
    }

    override fun getThomann(id: String, onComplete: (ThomannDao?, Exception?) -> Unit) {
        firebase.getThomann(id) { firestoreThomann, e ->
            val thomannDao = toThomannDao(firestoreThomann)
            onComplete(thomannDao, e)
        }
    }

    private fun toThomannDao(firestoreThomann: FirestoreThomann?) : ThomannDao? {
        var thomannDao: ThomannDao? = null
        if (firestoreThomann != null) {
            thomannDao = ThomannDao(
                firestoreThomann.id,
                firestoreThomann.userId,
                firestoreThomann.name,
                firestoreThomann.city,
                firestoreThomann.isLocked,
                firestoreThomann.creationDate,
                firestoreThomann.validUntil,
                firestoreThomann.users?.map { ftu -> toThomannUserDao(ftu)!! }
            )
        }
        return thomannDao
    }

    private fun toFirestoreThomann(thomannDao: ThomannDao?) : FirestoreThomann? {
        var firestoreThomann: FirestoreThomann? = null
        if (thomannDao != null) {
            val firestoreThomannUsers = ArrayList<FirestoreThomannUser>()
            if (thomannDao.users != null) {
                for (thomannUserDao in thomannDao.users!!) {
                    firestoreThomannUsers.add(toFirestoreThomannUser(thomannUserDao)!!)
                }
            }
            firestoreThomann = FirestoreThomann(
                thomannDao.id,
                thomannDao.userId,
                thomannDao.name,
                thomannDao.city,
                thomannDao.isLocked,
                thomannDao.creationDate,
                thomannDao.validUntil,
                firestoreThomannUsers
            )
        }
        return firestoreThomann
    }

    private fun toFirestoreThomannUser(thomannUserDao: ThomannUserDao?) : FirestoreThomannUser? {
        var firestoreThomannUser: FirestoreThomannUser? = null
        if (thomannUserDao != null) {
            firestoreThomannUser = FirestoreThomannUser(
                thomannUserDao.userId,
                thomannUserDao.userName,
                thomannUserDao.amount
            )
        }
        return firestoreThomannUser
    }

    private fun toThomannUserDao(firestoreThomannUser: FirestoreThomannUser?) : ThomannUserDao? {
        var thomannUserDao: ThomannUserDao? = null
        if (firestoreThomannUser != null) {
            thomannUserDao = ThomannUserDao(
                firestoreThomannUser.userId,
                firestoreThomannUser.userName,
                firestoreThomannUser.amount
            )
        }
        return thomannUserDao
    }
}