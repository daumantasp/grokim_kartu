package com.dauma.grokimkartu.data.users

import com.dauma.grokimkartu.models.users.AuthenticatedUser
import com.dauma.grokimkartu.models.users.LoginUser
import com.dauma.grokimkartu.models.users.RegistrationUser
import com.dauma.grokimkartu.models.users.User
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

    override fun registerUser(user: RegistrationUser, onComplete: (Boolean, String?, Exception?) -> Unit) {
        firebaseAuth
            .createUserWithEmailAndPassword(user.email, user.password)
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

    override fun addUserToFirestore(user: User, onComplete: (Boolean, Exception?) -> Unit) {
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

    override fun loginUser(user: LoginUser, onComplete: (Boolean, Exception?) -> Unit) {
        firebaseAuth
            .signInWithEmailAndPassword(user.email, user.password)
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

    override fun isUserLoggedIn(): Boolean {
        // TODO: read more at https://firebase.google.com/docs/auth/android/manage-users
        return firebaseAuth.currentUser != null
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
    override fun reauthenticateUser(user: LoginUser, onComplete: (Boolean, Exception?) -> Unit) {
        val credential = EmailAuthProvider.getCredential(user.email, user.password)
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

    override fun getAuthenticatedUserDataProfiles(): List<AuthenticatedUser> {
        val authenticatedUserByProfiles: MutableList<AuthenticatedUser> = mutableListOf()
        val user = firebaseAuth.currentUser
        user?.let {
            for (profile in it.providerData) {
                val authenticatedUserProfile = AuthenticatedUser(
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
}