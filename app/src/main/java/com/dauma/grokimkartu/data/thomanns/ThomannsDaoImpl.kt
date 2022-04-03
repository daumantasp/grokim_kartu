package com.dauma.grokimkartu.data.thomanns

import com.dauma.grokimkartu.data.firestore.Firestore
import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomann
import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomannActions
import com.dauma.grokimkartu.data.firestore.entities.FirestoreThomannUser
import com.dauma.grokimkartu.data.firestore.queries.CreateThomannQuery
import com.dauma.grokimkartu.data.firestore.queries.ReadThomannQuery
import com.dauma.grokimkartu.data.firestore.queries.ReadThomannsQuery
import com.dauma.grokimkartu.data.firestore.queries.UpdateThomannQuery
import com.dauma.grokimkartu.data.firestore.queries.composite.*
import com.dauma.grokimkartu.data.thomanns.entities.ThomannActionsDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannDao
import com.dauma.grokimkartu.data.thomanns.entities.ThomannUserDao
import com.google.firebase.firestore.FirebaseFirestore

class ThomannsDaoImpl(
    private val firebase: Firestore,
    private val firebaseFirestore: FirebaseFirestore
) : ThomannsDao {
    override fun createThomann(thomann: ThomannDao, onComplete: (Boolean, Exception?) -> Unit) {
        val firestoreThomann = toFirestoreThomann(thomann)
        CreateThomannQuery(firebaseFirestore)
            .withInput(firestoreThomann!!)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    override fun updateThomann(thomann: ThomannDao, onComplete: (Boolean, Exception?) -> Unit) {
        val firestoreThomann = toFirestoreThomann(thomann)
        UpdateThomannQuery(firebaseFirestore)
            .withId(thomann.id ?: "")
            .withInput(firestoreThomann!!)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    override fun deleteThomann(thomannId: String, userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        DeleteThomannQuery(firebaseFirestore)
            .withId(thomannId)
            .withInput(userId)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    override fun getThomanns(onComplete: (Boolean, List<ThomannDao>?, Exception?) -> Unit) {
        ReadThomannsQuery(firebaseFirestore)
            .onSuccess { firestoreThomanns ->
                val thomannsDaoList = firestoreThomanns?.map { ft -> toThomannDao(ft)!! }
                onComplete(true, thomannsDaoList, null)
            }
            .onFailure { exception ->
                onComplete(false, null, exception)
            }
            .execute()
    }

    override fun getThomann(id: String, onComplete: (ThomannDao?, Exception?) -> Unit) {
        ReadThomannQuery(firebaseFirestore)
            .withId(id)
            .onSuccess { firestoreThomann ->
                val thomannDao = toThomannDao(firestoreThomann)
                onComplete(thomannDao, null)
            }
            .onFailure { exception ->
                onComplete(null, exception)
            }
            .execute()
    }

    override fun getThomannActions(
        id: String,
        userId: String,
        onComplete: (ThomannActionsDao?, Exception?) -> Unit
    ) {
        ReadThomannActionsQuery(firebaseFirestore)
            .withId(id)
            .withInput(userId)
            .onSuccess { firestoreThomannActions ->
                val thomannActionsDao = toThomannActionsDao(firestoreThomannActions)
                onComplete(thomannActionsDao, null)
            }
            .onFailure { exception ->
                onComplete(null, exception)
            }
            .execute()
    }

    override fun joinThomann(
        id: String,
        user: ThomannUserDao,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        val firestoreThomannUser = toFirestoreThomannUser(user)!!
        JoinThomannQuery(firebaseFirestore)
            .withId(id)
            .withInput(firestoreThomannUser)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    override fun leaveThomann(
        id: String,
        userId: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        LeaveThomannQuery(firebaseFirestore)
            .withId(id)
            .withInput(userId)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    override fun lockThomann(
        thomannId: String,
        userId: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        LockThomannQuery(firebaseFirestore)
            .withId(thomannId)
            .withInput(userId)
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    override fun unlockThomann(
        thomannId: String,
        userId: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        firebase.unlockThomann(thomannId, userId, onComplete)
    }

    override fun kickUserFromThomann(
        thomannId: String,
        userId: String,
        userToKickId: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        KickUserFromThomannQuery(firebaseFirestore)
            .withId(thomannId)
            .withInput(KickUserFromThomanQueryInput(userId, userToKickId))
            .onSuccess { _ ->
                onComplete(true, null)
            }
            .onFailure { exception ->
                onComplete(false, exception)
            }
            .execute()
    }

    private fun toThomannDao(firestoreThomann: FirestoreThomann?) : ThomannDao? {
        var thomannDao: ThomannDao? = null
        if (firestoreThomann != null) {
            thomannDao = ThomannDao(
                firestoreThomann.id,
                firestoreThomann.userId,
                firestoreThomann.name,
                firestoreThomann.city,
                firestoreThomann.locked,
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
                thomannUserDao.thomannId,
                thomannUserDao.amount,
                thomannUserDao.joinDate
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
                firestoreThomannUser.thomannId,
                firestoreThomannUser.amount,
                firestoreThomannUser.joinDate
            )
        }
        return thomannUserDao
    }

    private fun toThomannActionsDao(firestoreThomannActions: FirestoreThomannActions?) : ThomannActionsDao? {
        var thomannActionsDao: ThomannActionsDao? = null
        if (firestoreThomannActions != null) {
            thomannActionsDao = ThomannActionsDao(
                firestoreThomannActions.thomannId,
                firestoreThomannActions.isAccessible,
                firestoreThomannActions.isJoinable,
                firestoreThomannActions.isUpdatable
            )
        }
        return thomannActionsDao
    }
}