import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dauma.grokimkartu.general.utils.image.ImageUtils
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class FirebaseStorageImpl(
    private val firebaseStorage: FirebaseStorage,
    private val imageUtils: ImageUtils,
)
    : com.dauma.grokimkartu.data.firestore.FirebaseStorage {
    private val storageRef = firebaseStorage.reference
    private val usersRef = storageRef.child(usersReference)

    companion object {
        private const val usersReference = "users"
        private const val profilePhotoChild = "profilePhoto"
        private const val profilePhotoIconChild = "${profilePhotoChild}_icon"
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
                this.uploadProfilePhotoIcon(userId, photo, onComplete)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    override fun deleteProfilePhoto(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        val profilePhotoRef = usersRef.child(userId).child("$profilePhotoChild.$profilePhotoFormatType")

        profilePhotoRef.delete()
            .addOnSuccessListener { _ ->
                this.deleteProfilePhotoIcon(userId, onComplete)
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

    override fun downloadProfilePhotoIcon(userId: String, onComplete: (Bitmap?, Exception?) -> Unit) {
        val profilePhotoIconRef = usersRef.child(userId).child("$profilePhotoIconChild.$profilePhotoFormatType")

        profilePhotoIconRef.getBytes(profilePhotoMaxSize)
            .addOnSuccessListener { taskSnapshot ->
                val bitmap = BitmapFactory.decodeByteArray(taskSnapshot, 0, taskSnapshot.size)
                onComplete(bitmap, null)
            }
            .addOnFailureListener { e ->
                onComplete(null, e)
            }
    }

    private fun uploadProfilePhotoIcon(userId: String, photo: Bitmap, onComplete: (Boolean, Exception?) -> Unit) {
        val profilePhotoIcon = imageUtils.scaleImage(photo, 100, 100)
        val profilePhotIconRef = usersRef.child(userId).child("$profilePhotoIconChild.$profilePhotoFormatType")
        val baos = ByteArrayOutputStream()
        profilePhotoIcon.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        profilePhotIconRef.putBytes(data)
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }

    }

    private fun deleteProfilePhotoIcon(userId: String, onComplete: (Boolean, Exception?) -> Unit) {
        val profilePhotoIconRef = usersRef.child(userId).child("$profilePhotoIconChild.$profilePhotoFormatType")

        profilePhotoIconRef.delete()
            .addOnSuccessListener { _ ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }
}