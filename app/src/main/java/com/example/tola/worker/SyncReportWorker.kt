package com.example.tola.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SyncReportWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val reportId = inputData.getString(KEY_REPORT_ID) ?: return Result.failure()
        val expectedOwnerUid = inputData.getString(KEY_OWNER_UID) ?: return Result.failure()
        val itemName = inputData.getString(KEY_ITEM_NAME) ?: ""
        val category = inputData.getString(KEY_CATEGORY) ?: ""
        val description = inputData.getString(KEY_DESCRIPTION) ?: ""
        val location = inputData.getString(KEY_LOCATION) ?: ""
        val date = inputData.getString(KEY_DATE) ?: ""
        val time = inputData.getString(KEY_TIME) ?: ""
        val type = inputData.getString(KEY_TYPE) ?: "LOST"
        val imageUrl = inputData.getString(KEY_IMAGE_URL) ?: ""
        val linkedItemId = inputData.getString(KEY_LINKED_ITEM_ID)

        val currentAuth = try { FirebaseAuth.getInstance() } catch (_: Exception) { null }
        val currentFirestore = try { FirebaseFirestore.getInstance() } catch (_: Exception) { null }

        if (currentAuth == null || currentFirestore == null) {
            Log.e(TAG, "Firebase services not available. Retrying...")
            return Result.retry()
        }

        val currentUser = currentAuth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "User logged out before background sync. Failing task to prevent cross-user submission.")
            return Result.failure()
        }

        if (currentUser.uid != expectedOwnerUid) {
            Log.e(TAG, "Authenticated user (${currentUser.uid}) does not match task owner ($expectedOwnerUid). Aborting to prevent uploading under wrong account.")
            return Result.failure()
        }

        return try {
            // Idempotency check: verify if document already exists to prevent duplicate report creation on retries
            val existingDoc = currentFirestore.collection("reports")
                .document(reportId)
                .get()
                .await()

            if (existingDoc.exists()) {
                Log.d(TAG, "Report $reportId already exists in Firestore. Sync complete.")
                return Result.success()
            }

            val reportData = hashMapOf<String, Any>(
                "itemName" to itemName,
                "category" to category,
                "description" to description,
                "location" to location,
                "date" to date,
                "time" to time,
                "type" to type,
                "ownerUid" to currentUser.uid,
                "status" to "OPEN",
                "imageUrl" to imageUrl,
                "createdAt" to FieldValue.serverTimestamp()
            )

            if (!linkedItemId.isNullOrBlank()) {
                reportData["linkedItemId"] = linkedItemId
            }

            currentFirestore.collection("reports")
                .document(reportId)
                .set(reportData)
                .await()

            Log.d(TAG, "Successfully synced report $reportId to Firestore for user ${currentUser.uid}")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync report $reportId to Firestore: ${e.localizedMessage}", e)
            Result.retry()
        }
    }

    companion object {
        const val TAG = "SyncReportWorker"
        const val KEY_REPORT_ID = "KEY_REPORT_ID"
        const val KEY_OWNER_UID = "KEY_OWNER_UID"
        const val KEY_ITEM_NAME = "KEY_ITEM_NAME"
        const val KEY_CATEGORY = "KEY_CATEGORY"
        const val KEY_DESCRIPTION = "KEY_DESCRIPTION"
        const val KEY_LOCATION = "KEY_LOCATION"
        const val KEY_DATE = "KEY_DATE"
        const val KEY_TIME = "KEY_TIME"
        const val KEY_TYPE = "KEY_TYPE"
        const val KEY_IMAGE_URL = "KEY_IMAGE_URL"
        const val KEY_LINKED_ITEM_ID = "KEY_LINKED_ITEM_ID"
    }
}
