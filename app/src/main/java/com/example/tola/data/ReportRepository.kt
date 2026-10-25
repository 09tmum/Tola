package com.example.tola.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReportRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
) {
    suspend fun submitReport(
        itemName: String,
        category: String,
        description: String,
        location: String,
        date: String,
        time: String,
        isLost: Boolean
    ): String {
        val user = auth.currentUser
            ?: throw IllegalStateException(
                "You must be logged in to submit a report."
            )

        val reportData = hashMapOf(
            "itemName" to itemName.trim(),
            "category" to category,
            "description" to description.trim(),
            "location" to location.trim(),
            "date" to date,
            "time" to time,
            "type" to if (isLost) "LOST" else "FOUND",
            "ownerUid" to user.uid,
            "status" to "OPEN",
            "createdAt" to FieldValue.serverTimestamp()
        )

        val document = firestore.collection("reports")
            .add(reportData)
            .await()

        return document.id
    }
}
