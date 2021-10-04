package com.dauma.grokimkartu.data.users

import com.dauma.grokimkartu.models.users.RegistrationUser
import com.dauma.grokimkartu.models.users.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UsersDaoImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
) : UsersDao {

    companion object {
        private const val usersCollection = "users"
    }

    override fun registerUser(user: RegistrationUser, onComplete: (Boolean, User?) -> Unit) {
        firebaseAuth
            .createUserWithEmailAndPassword(user.email!!, user.password!!)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    val id = firebaseAuth.currentUser?.uid
                    if (id != null) {
                        val userToSaveInFirestore = User(id, user.name)
                        firebaseFirestore
                            .collection(UsersDaoImpl.usersCollection)
                            .document(id)
                            .set(userToSaveInFirestore)
                            .addOnSuccessListener { _ ->
                                onComplete(true, userToSaveInFirestore)
                            }
                            .addOnFailureListener { _ ->
                                onComplete(false, null)
                            }
                    } else {
                        // This situation should never occur
                        onComplete(false, null)
                    }
                } else {
                    onComplete(false, null)
                }
            }
            .addOnFailureListener { _ ->
                onComplete(false, null)
            }
    }
}