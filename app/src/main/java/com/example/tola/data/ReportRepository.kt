package com.example.tola.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query

class ReportRepository(
    private val authProvider: () -> FirebaseAuth? = {
        try { FirebaseAuth.getInstance() } catch (_: Exception) { null }
    },
    private val firestoreProvider: () -> FirebaseFirestore? = {
        try { FirebaseFirestore.getInstance() } catch (_: Exception) { null }
    }
) {
    private val auth: FirebaseAuth?
        get() = authProvider()

    private val firestore: FirebaseFirestore?
        get() = firestoreProvider()

    suspend fun submitReport(
        itemName: String,
        category: String,
        description: String,
        location: String,
        date: String,
        time: String,
        isLost: Boolean
    ): String {
        val currentAuth = auth
            ?: throw IllegalStateException("Firebase Authentication is not initialized.")
        val currentFirestore = firestore
            ?: throw IllegalStateException("Firebase Firestore is not initialized.")

        val user = currentAuth.currentUser
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

        val document = currentFirestore.collection("reports")
            .add(reportData)
            .await()

        return document.id
    }

    suspend fun submitFoundReportForLinkedItem(
        linkedItemId: String,
        locationFound: String,
        dateFound: String,
        timeFound: String,
        additionalDetails: String
    ): String {
        val currentAuth = auth
            ?: throw IllegalStateException("Firebase Authentication is not initialized.")
        val currentFirestore = firestore
            ?: throw IllegalStateException("Firebase Firestore is not initialized.")

        val user = currentAuth.currentUser
            ?: throw IllegalStateException("You must be logged in to submit a report.")

        val originalReport = try {
            getReportById(linkedItemId)
        } catch (_: Exception) {
            null
        }

        val reportData = hashMapOf(
            "linkedItemId" to linkedItemId,
            "itemName" to (originalReport?.itemName?.ifBlank { "Found Item" } ?: "Found Item"),
            "category" to (originalReport?.category ?: "Other"),
            "description" to additionalDetails.trim().ifBlank { originalReport?.description ?: "" },
            "location" to locationFound.trim(),
            "date" to dateFound,
            "time" to timeFound,
            "type" to "FOUND",
            "ownerUid" to user.uid,
            "status" to "OPEN",
            "createdAt" to FieldValue.serverTimestamp()
        )

        val document = currentFirestore.collection("reports")
            .add(reportData)
            .await()

        return document.id
    }

    suspend fun getReports(): List<LostFoundReport> {
        val currentAuth = auth ?: return emptyList()
        val currentFirestore = firestore ?: return emptyList()

        if (currentAuth.currentUser == null) {
            return emptyList()
        }

        val snapshot = currentFirestore.collection("reports")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.documents.map { document ->
            LostFoundReport(
                id = document.id,
                itemName = document.getString("itemName") ?: "",
                category = document.getString("category") ?: "",
                description = document.getString("description") ?: "",
                location = document.getString("location") ?: "",
                date = document.getString("date") ?: "",
                time = document.getString("time") ?: "",
                type = document.getString("type") ?: "LOST",
                ownerUid = document.getString("ownerUid") ?: "",
                status = document.getString("status") ?: "OPEN"
            )
        }
    }

    suspend fun getUserReports(): List<LostFoundReport> {
        val currentAuth = auth ?: return emptyList()
        val currentFirestore = firestore ?: return emptyList()

        val user = currentAuth.currentUser ?: return emptyList()

        return try {
            val snapshot = currentFirestore.collection("reports")
                .whereEqualTo("ownerUid", user.uid)
                .get()
                .await()

            snapshot.documents.map { document ->
                LostFoundReport(
                    id = document.id,
                    itemName = document.getString("itemName") ?: "",
                    category = document.getString("category") ?: "",
                    description = document.getString("description") ?: "",
                    location = document.getString("location") ?: "",
                    date = document.getString("date") ?: "",
                    time = document.getString("time") ?: "",
                    type = document.getString("type") ?: "LOST",
                    ownerUid = document.getString("ownerUid") ?: "",
                    status = document.getString("status") ?: "OPEN"
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getReportById(reportId: String): LostFoundReport? {
        val currentAuth = auth ?: return null
        val currentFirestore = firestore ?: return null

        if (currentAuth.currentUser == null) {
            return null
        }

        val document = currentFirestore.collection("reports")
            .document(reportId)
            .get()
            .await()

        if (!document.exists()) {
            return null
        }

        return LostFoundReport(
            id = document.id,
            itemName = document.getString("itemName") ?: "",
            category = document.getString("category") ?: "",
            description = document.getString("description") ?: "",
            location = document.getString("location") ?: "",
            date = document.getString("date") ?: "",
            time = document.getString("time") ?: "",
            type = document.getString("type") ?: "LOST",
            ownerUid = document.getString("ownerUid") ?: "",
            status = document.getString("status") ?: "OPEN"
        )
    }
}
