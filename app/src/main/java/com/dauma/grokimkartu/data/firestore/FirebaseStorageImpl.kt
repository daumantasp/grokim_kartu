import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class FirebaseStorageImpl(private val firebaseStorage: FirebaseStorage)
    : com.dauma.grokimkartu.data.firestore.FirebaseStorage {
    private val storageRef = firebaseStorage.reference
    private val usersRef = storageRef.child(usersReference)

    companion object {
        private const val usersReference = "users"
        private const val profilePhotoChild = "profilePhoto"
        private const val profilePhotoFormatType = "jpg"
        private const val ONE_MEGABYTE: Long = 1024L * 1024L
        private const val profilePhotoMaxSize = ONE_MEGABYTE
    }

    override fun uploadProfilePhoto(userId: String, photo: Bitmap, onComplete: (Boolean, Exception?) -> Unit) {
        val profilePhotoRef = usersRef.child(userId).child("$profilePhotoChild.$profilePhotoFormatType")
        val baos = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        profilePhotoRef.putBytes(data)
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun downloadProfilePhoto(userId: String, onComplete: (Bitmap?, Exception?) -> Unit) {
        val profilePhotoRef = usersRef.child(userId).child("$profilePhotoChild.$profilePhotoFormatType")

        profilePhotoRef.getBytes(profilePhotoMaxSize)
            .addOnSuccessListener { taskSnapshot ->
                val bitmap = BitmapFactory.decodeByteArray(taskSnapshot, 0, taskSnapshot.size)
                onComplete(bitmap, null)
            }
            .addOnFailureListener { e ->
                onComplete(null, e)
            }
    }

    override fun deleteProfilePhoto(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        val profilePhotoRef = usersRef.child(userId).child("$profilePhotoChild.$profilePhotoFormatType")

        profilePhotoRef.delete()
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }
}