package com.dauma.grokimkartu.data.firestore

import android.graphics.Bitmap

interface FirebaseStorage {
    fun uploadProfilePhoto(userId: String, photo: Bitmap, onComplete: (Boolean, Exception?) -> Unit)
    fun downloadProfilePhoto(userId: String, onComplete: (Bitmap?, Exception?) -> Unit)
    fun deleteProfilePhoto(userId: String, onComplete: (Boolean, Exception?) -> Unit)
}