package com.example.tola.data

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseStorageRepository(
    private val storageProvider: () -> FirebaseStorage? = {
        try { FirebaseStorage.getInstance() } catch (_: Exception) { null }
    }
) {
    private val storage: FirebaseStorage?
        get() = storageProvider()

    suspend fun uploadImage(
        context: Context,
        imageUri: Uri,
        folder: String = "reports"
    ): String {
        val currentStorage = storage ?: return imageUri.toString()
        val filename = "${UUID.randomUUID()}.jpg"
        val storageRef = currentStorage.reference.child("$folder/$filename")

        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return imageUri.toString()
            storageRef.putStream(inputStream).await()
            val downloadUrl = storageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (_: Exception) {
            // Fallback safely to URI string if storage upload is unconfigured or fails
            imageUri.toString()
        }
    }
}
