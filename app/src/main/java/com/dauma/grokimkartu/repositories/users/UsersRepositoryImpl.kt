package com.dauma.grokimkartu.repositories.users

import com.dauma.grokimkartu.data.auth.AuthDao
import com.dauma.grokimkartu.data.users.UsersDao
import com.dauma.grokimkartu.data.auth.entities.AuthUser
import com.dauma.grokimkartu.data.users.entities.FirestoreProfile
import com.dauma.grokimkartu.data.users.entities.FirestoreUser
import com.dauma.grokimkartu.repositories.users.entities.Profile
import com.dauma.grokimkartu.repositories.users.entities.User
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlin.Exception

class UsersRepositoryImpl(
    private val authDao: AuthDao,
    private val usersDao: UsersDao,
) : UsersRepository {
    override fun isUserLoggedIn(): Boolean {
        return authDao.getUserId() != null
    }

    override fun registerUser(email: String, password: String, name: String, onComplete: (Boolean, AuthenticationError?) -> Unit) {
        if (isUserLoggedIn() == false) {
            authDao.registerUser(email, password) { isSuccessful, userId, e ->
                if (isSuccessful && userId != null) {
                    this.authDao.updateUser(name) { isSuccessful, e ->
                        if (isSuccessful) {
                            val userToSaveInFirestore = FirestoreUser(userId, true, null)
                            this.usersDao.setFirestoreUser(userToSaveInFirestore) { isSuccessful, e ->
                                if (isSuccessful) {
                                    onComplete(true, null)
                                } else {
                                    val error = AuthenticationError(6)
                                    onComplete(false, error)
                                }
                            }
                        } else {
                            val error = AuthenticationError(5)
                            onComplete(false, error)
                        }
                    }
                } else {
                    val error: AuthenticationError
                    if (e is FirebaseAuthUserCollisionException) {
                        error = AuthenticationError(7)
                    } else if (e is FirebaseAuthInvalidCredentialsException) {
                        error = AuthenticationError(8)
                    } else if (e is FirebaseAuthWeakPasswordException) {
                        error = AuthenticationError(9)
                    } else {
                        error = AuthenticationError(5)
                    }
                    onComplete(false, error)
                }
            }
        } else {
            val error = AuthenticationError(1)
            throw AuthenticationException(error)
        }
    }

    override fun sendEmailVerification() {
        if (isUserLoggedIn()) {
            authDao.sendEmailVerification()
        } else {
            val error = AuthenticationError(2)
            throw AuthenticationException(error)
        }
    }

    override fun isEmailVerified(): Boolean {
        if (isUserLoggedIn()) {
            return authDao.isEmailVerified()
        } else {
            val error = AuthenticationError(2)
            throw AuthenticationException(error)
        }
    }

    override fun loginUser(email: String, password: String, onComplete: (Boolean, AuthenticationError?) -> Unit) {
        if (isUserLoggedIn() == false) {
            authDao.loginUser(email, password) { isSuccessful, e ->
                if (isSuccessful) {
                    if (isEmailVerified()) {
                        onComplete(true, null)
                    } else {
                        logOut()
                        onComplete(false, AuthenticationError(10))
                    }
                } else {
                    val error: AuthenticationError
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        error = AuthenticationError(3)
                    } else if (e is FirebaseAuthInvalidUserException) {
                        error = AuthenticationError(4)
                    } else {
                        error = AuthenticationError(5)
                    }
                    onComplete(false, error)
                }
            }
        } else {
            val error = AuthenticationError(1)
            throw AuthenticationException(error)
        }
        // READ MORE AT: https://www.techotopia.com/index.php?title=Handling_Firebase_Authentication_Errors_and_Failures&mobileaction=toggle_view_mobile
    }

    override fun logOut() {
        if (isUserLoggedIn()) {
            authDao.logOut()
        } else {
            val error = AuthenticationError(2)
            throw AuthenticationException(error)
        }
    }

    override fun sendPasswordResetEmail(
        email: String,
        onComplete: (Boolean, AuthenticationError?) -> Unit
    ) {
        if (isUserLoggedIn() == false) {
            authDao.sendPasswordResetEmail(email) { isSuccessful, e ->
                if (isSuccessful) {
                    onComplete(true, null)
                } else {
                    val error: AuthenticationError
                    if (e is FirebaseAuthInvalidUserException) {
                        error = AuthenticationError(4)
                    } else {
                        error = AuthenticationError(5)
                    }
                    onComplete(false, error)
                }
            }
        } else {
            val error = AuthenticationError(1)
            throw AuthenticationException(error)
        }
    }

    override fun deleteUser(onComplete: (Boolean, AuthenticationError?) -> Unit) {
        if (isUserLoggedIn()) {
            authDao.deleteUser { isSuccessful, id, e ->
                if (isSuccessful && id != null) {
                    this.usersDao.deleteFirestoreUser(id) { isSuccessful, e ->
                        if (isSuccessful) {
                            onComplete(true, null)
                        } else {
                            onComplete(false, AuthenticationError(11))
                        }
                    }
                } else {
                    onComplete(false, AuthenticationError(5))
                }
            }
        } else {
            val error = AuthenticationError(2)
            throw AuthenticationException(error)
        }
    }

    // TODO: Same as login. Refactor?
    override fun reauthenticateUser(email: String, password: String, onComplete: (Boolean, AuthenticationError?) -> Unit) {
        authDao.reauthenticateUser(email, password) { isSuccessful, e ->
            if (isSuccessful) {
                if (isEmailVerified()) {
                    onComplete(true, null)
                } else {
                    logOut()
                    onComplete(false, AuthenticationError(10))
                }
            } else {
                val error: AuthenticationError
                if (e is FirebaseAuthInvalidCredentialsException) {
                    error = AuthenticationError(3)
                } else if (e is FirebaseAuthInvalidUserException) {
                    error = AuthenticationError(4)
                } else {
                    error = AuthenticationError(5)
                }
                onComplete(false, error)
            }
        }
    }

    override fun getUserData(onComplete: (User?, Exception?) -> Unit) {
        if (isUserLoggedIn()) {
            val userDataProfiles = authDao.getUserDataProfiles()
            var authUser: AuthUser? = null
            val userId = authDao.getUserId()
            if (userDataProfiles.count() > 0) {
                authUser = userDataProfiles[0]
            } else {
                val error = AuthenticationError(5)
                throw AuthenticationException(error)
            }
            usersDao.getFirestoreUser(userId!!) { firestoreUser, e ->
                if (firestoreUser != null) {
                    val user = User(
                        authUser.providerId,
                        authUser.id,
                        authUser.name,
                        authUser.email,
                        authUser.photoUrl,
                        firestoreUser.visible
                    )
                    onComplete(user, null)
                } else {
                    val error = AuthenticationError(2)
                    onComplete(null, AuthenticationException(error))
                }
            }
        } else {
            val error = AuthenticationError(2)
            throw AuthenticationException(error)
        }
    }

    override fun setUserData(user: User, onComplete: (Boolean, Exception?) -> Unit) {
        if (isUserLoggedIn()) {
            val firestoreUser = FirestoreUser(authDao.getUserId() ?: "", user.visible, null)
            usersDao.setFirestoreUser(firestoreUser) { isSuccessful, e ->
                if (isSuccessful) {
                    onComplete(true, null)
                } else {
                    val error = AuthenticationError(2)
                    onComplete(false, AuthenticationException(error))
                }
            }
        } else {
            val error = AuthenticationError(2)
            throw AuthenticationException(error)
        }
    }

    override fun getUserProfile(onComplete: (Profile?, Exception?) -> Unit) {
        if (isUserLoggedIn()) {
            val userId = authDao.getUserId()
            usersDao.getFirestoreUser(userId!!) { firestoreUser, e ->
                if (firestoreUser != null) {
                    val profile = Profile(
                        firestoreUser.profile?.instrument ?: "",
                        firestoreUser.profile?.description
                    )
                    onComplete(profile, null)
                } else {
                    val error = AuthenticationError(2)
                    onComplete(null, AuthenticationException(error))
                }
            }
        } else {
            val error = AuthenticationError(2)
            throw AuthenticationException(error)
        }
    }

    override fun setUserProfile(profile: Profile, onComplete: (Boolean, Exception?) -> Unit) {
        // TODO
//        if (isUserLoggedIn()) {
//            val firestorePlayer = FirestoreProfile(profile.instrument, profile.description)
//            usersDao.setFirestoreUser(firestoreUser) { isSuccessful, e ->
//                if (isSuccessful) {
//                    onComplete(true, null)
//                } else {
//                    val error = AuthenticationError(2)
//                    onComplete(false, AuthenticationException(error))
//                }
//            }
//        } else {
//            val error = AuthenticationError(2)
//            throw AuthenticationException(error)
//        }
    }

    override fun updatePassword(newPassword: String, onComplete: (Boolean, AuthenticationError?) -> Unit) {
        if (isUserLoggedIn()) {
            authDao.updateUserPassword(newPassword) { isSuccessful, e ->
                if (isSuccessful) {
                    onComplete(true, null)
                } else {
                    val error: AuthenticationError
                    if (e is FirebaseAuthWeakPasswordException) {
                        error = AuthenticationError(9)
                    } else {
                        error = AuthenticationError(5)
                    }
                    onComplete(false, error)
                }
            }
        } else {
            val error = AuthenticationError(2)
            throw AuthenticationException(error)
        }
    }
}

class AuthenticationException(error: AuthenticationError)
    : Exception(error.message) {
}

class AuthenticationError(val code: Int) {
    val message: String = when(code) {
        1 -> USER_ALREADY_LOGGED_IN
        2 -> USER_NOT_LOGGED_IN
        3 -> INVALID_PASSWORD
        4 -> INVALID_EMAIL
        5 -> SOMETHING_FAILED
        6 -> FAILED_TO_ADD_USER_TO_FIRESTORE
        7 -> EMAIL_ALREADY_REGISTERED
        8 -> EMAIL_INCORRECT_FORMAT
        9 -> PASSWORD_TOO_WEAK
        10 -> EMAIL_NOT_VERIFIED
        11 -> FAILED_TO_DELETE_USER_FROM_FIRESTORE
        else -> ""
    }

    companion object {
        const val USER_ALREADY_LOGGED_IN = "User is already logged in!"
        const val USER_NOT_LOGGED_IN = "User is not logged in!"
        const val INVALID_PASSWORD = "Invalid password"
        const val INVALID_EMAIL = "Incorrect email address"
        const val SOMETHING_FAILED = "Something failed"
        const val FAILED_TO_ADD_USER_TO_FIRESTORE = "Failed to add a registered user to Firestore!"
        const val EMAIL_ALREADY_REGISTERED = "Email already registered!"
        const val EMAIL_INCORRECT_FORMAT = "Email is in incorrect format!"
        const val PASSWORD_TOO_WEAK = "Password is too weak!"
        const val EMAIL_NOT_VERIFIED = "Email is not verified!"
        const val FAILED_TO_DELETE_USER_FROM_FIRESTORE = "Failed to delete current user from Firestore!"
    }
}