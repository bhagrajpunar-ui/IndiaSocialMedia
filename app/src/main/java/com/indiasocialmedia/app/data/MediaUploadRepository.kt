package com.indiasocialmedia.app.data

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

/**
 * Upload a selected photo/video Uri to Firebase Storage and return its download URL.
 * Configure Storage rules to require an authenticated owner before using in production.
 */
class MediaUploadRepository(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    fun uploadPostMedia(uid: String, uri: Uri, fileExtension: String, onResult: (Result<String>) -> Unit) {
        val safeExtension = fileExtension.trim().lowercase().removePrefix(".").ifBlank { "jpg" }
        val path = "post_media/$uid/${UUID.randomUUID()}.$safeExtension"
        val reference = storage.reference.child(path)
        reference.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception ?: IllegalStateException("Upload failed")
                reference.downloadUrl
            }
            .addOnSuccessListener { downloadUri -> onResult(Result.success(downloadUri.toString())) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }
}
