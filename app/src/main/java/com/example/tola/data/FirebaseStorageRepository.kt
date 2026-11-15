package com.example.tola.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseStorageRepository(
    private val storageProvider: () -> FirebaseStorage? = {
        try {
            FirebaseStorage.getInstance("gs://tola-campus-lost-and-found.firebasestorage.app")
        } catch (_: Exception) {
            try { FirebaseStorage.getInstance() } catch (_: Exception) { null }
        }
    }
) {
    private val storage: FirebaseStorage?
        get() = storageProvider()

    suspend fun uploadImage(
        context: Context,
        imageUri: Uri,
        folder: String = "reports"
    ): String {
        val currentStorage = storage ?: throw IllegalStateException("Firebase Storage is not initialized.")
        val filename = "${UUID.randomUUID()}.jpg"
        val storageRef = currentStorage.reference.child("$folder/$filename")

        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: throw IllegalStateException("Could not open image file.")

            Log.d("FirebaseStorage", "Uploading image to $folder/$filename...")
            storageRef.putStream(inputStream).await()
            val downloadUrl = storageRef.downloadUrl.await()
            Log.d("FirebaseStorage", "Upload successful! Public URL: $downloadUrl")
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("FirebaseStorage", "Failed to upload image to Firebase Storage: ${e.localizedMessage}", e)
            throw IllegalStateException("Cloud photo upload failed: ${e.localizedMessage}. Please check Firebase Storage rules.")
        }
    }
}
