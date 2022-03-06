package com.dauma.grokimkartu.data.thomanns

import com.dauma.grokimkartu.data.firestore.Firestore
import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.thomanns.entities.ThomannDao

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

    private fun toThomannDao(firestoreThomann: FirestoreThomann?) : ThomannDao? {
        var thomannDao: ThomannDao? = null
        if (firestoreThomann != null) {
            thomannDao = ThomannDao(
                firestoreThomann.id,
                firestoreThomann.name,
                firestoreThomann.city,
                firestoreThomann.isLocked,
                firestoreThomann.creationDate,
                firestoreThomann.validUntil
            )
        }
        return thomannDao
    }

    private fun toFirestoreThomann(thomannDao: ThomannDao?) : FirestoreThomann? {
        var firestoreThomann: FirestoreThomann? = null
        if (thomannDao != null) {
            firestoreThomann = FirestoreThomann(
                thomannDao.id,
                thomannDao.name,
                thomannDao.city,
                thomannDao.isLocked,
                thomannDao.creationDate,
                thomannDao.validUntil
            )
        }
        return firestoreThomann
    }
}