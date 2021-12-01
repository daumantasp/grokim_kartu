package com.dauma.grokimkartu.data.users

import com.dauma.grokimkartu.data.users.entities.AuthenticationUser
import com.dauma.grokimkartu.data.users.entities.FirestoreUser
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UsersDaoImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
) : UsersDao {

    companion object {
        private const val usersCollection = "users"
    }

    override fun registerUser(email: String, password: String, onComplete: (Boolean, String?, Exception?) -> Unit) {
        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val id = firebaseAuth.currentUser?.uid
                    if (id != null) {
                        onComplete(true, id, null)
                    } else {
                        // This situation should never occur
                        onComplete(false, null, Exception("MISSING REGISTERED USER ID"))
                    }
                } else {
                    onComplete(false, null, task.exception)
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, null, e)
            }
    }

    override fun setFirestoreUser(user: FirestoreUser, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseFirestore
            .collection(UsersDaoImpl.usersCollection)
            .document(user.id)
            .set(user)
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun getFirestoreUser(userId: String, onComplete: (FirestoreUser?, Exception?) -> Unit) {
        firebaseFirestore
            .collection(UsersDaoImpl.usersCollection)
            .document(userId)
            .get()
            .addOnSuccessListener { userDocumentSnapshot ->
                if (userDocumentSnapshot.exists()) {
                    val user = userDocumentSnapshot.toObject(FirestoreUser::class.java)
                    onComplete(user, null)
                } else {
                    onComplete(null, Exception("USER WAS NOT FOUND"))
                }
            }
            .addOnFailureListener { e ->
                onComplete(null, e)
            }
    }

    override fun deleteFirestoreUser(
        userId: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        firebaseFirestore
            .collection(UsersDaoImpl.usersCollection)
            .document(userId)
            .delete()
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun loginUser(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception)
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun getAuthenticatedUserId(): String? {
        // TODO: read more at https://firebase.google.com/docs/auth/android/manage-users
        return firebaseAuth.currentUser?.uid
    }

    override fun logOut() {
        firebaseAuth.signOut()
    }

    override fun isEmailVerified(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }

    override fun sendEmailVerification() {
        firebaseAuth.currentUser?.sendEmailVerification()
    }

    override fun sendPasswordResetEmail(email: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseAuth
            .sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception)
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun deleteUser(onComplete: (Boolean, String?, Exception?) -> Unit) {
        val id = firebaseAuth.currentUser?.uid
        if (id == null) {
            // This situation should never occur
            onComplete(false, null, Exception("MISSING LOGGED IN USER ID"))
        }
        firebaseAuth.currentUser
            ?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, id, null)
                } else {
                    onComplete(false, null, task.exception)
                }
            }
            ?.addOnFailureListener { e ->
                onComplete(false, null, e)
            }
    }

    override fun reauthenticateUser(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit) {
        val credential = EmailAuthProvider.getCredential(email, password)
        firebaseAuth.currentUser
            ?.reauthenticate(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception)
                }
            }
            ?.addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun getUserDataProfiles(): List<AuthenticationUser> {
        val authenticatedUserByProfiles: MutableList<AuthenticationUser> = mutableListOf()
        val user = firebaseAuth.currentUser
        user?.let {
            for (profile in it.providerData) {
                val authenticatedUserProfile = AuthenticationUser(
                    profile.providerId,
                    profile.uid,
                    profile.displayName,
                    profile.email,
                    profile.photoUrl
                )
                authenticatedUserByProfiles.add(authenticatedUserProfile)
            }
        }
        return authenticatedUserByProfiles
    }

    override fun updateUserPassword(newPassword: String, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseAuth.currentUser
            ?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception)
                }
            }
            ?.addOnFailureListener { e ->
                onComplete(false, e)
            }
    }
}