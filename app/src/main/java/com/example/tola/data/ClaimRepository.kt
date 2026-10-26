package com.example.tola.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.tola.ui.screens.AdminClaim
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class ClaimRepository(
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

    suspend fun submitClaim(
        itemId: String,
        proofDescription: String
    ): String {
        val currentAuth = auth
            ?: throw IllegalStateException("Please log in before submitting a claim.")
        val currentFirestore = firestore
            ?: throw IllegalStateException("Firebase Firestore is not initialized.")

        val user = currentAuth.currentUser
            ?: throw IllegalStateException("Please log in before submitting a claim.")

        val proof = proofDescription.trim()

        require(itemId.isNotBlank()) {
            "The item ID is missing."
        }

        require(proof.isNotEmpty() && proof.length <= 400) {
            "Please provide ownership evidence between 1 and 400 characters."
        }

        val report = currentFirestore.collection("reports")
            .document(itemId)
            .get()
            .await()

        check(report.exists() && report.getString("type") == "FOUND") {
            "This found item is unavailable for claiming."
        }

        check(report.getString("ownerUid") != user.uid) {
            "You cannot claim an item that you reported yourself."
        }

        val claimData = hashMapOf(
            "itemId" to itemId,
            "claimantUid" to user.uid,
            "proofDescription" to proof,
            "status" to "PENDING",
            "createdAt" to FieldValue.serverTimestamp()
        )

        val claimRef = currentFirestore.collection("claims")
            .add(claimData)
            .await()

        return claimRef.id
    }

    suspend fun getUserPendingClaimsCount(): Int {
        val currentAuth = auth ?: return 0
        val currentFirestore = firestore ?: return 0
        val user = currentAuth.currentUser ?: return 0

        return try {
            val snapshot = currentFirestore.collection("claims")
                .whereEqualTo("claimantUid", user.uid)
                .whereEqualTo("status", "PENDING")
                .get()
                .await()
            snapshot.size()
        } catch (_: Exception) {
            0
        }
    }
    suspend fun getPendingAdminClaims(): List<AdminClaim> {
        val currentAuth = auth
            ?: throw IllegalStateException("Firebase Authentication is not initialized.")

        val currentFirestore = firestore
            ?: throw IllegalStateException("Firebase Firestore is not initialized.")

        val user = currentAuth.currentUser
            ?: throw IllegalStateException("Please log in to view claims.")

        val claimsSnapshot = currentFirestore.collection("claims")
            .whereEqualTo("status", "PENDING")
            .get()
            .await()

        val dateFormatter = SimpleDateFormat(
            "dd MMM yyyy, HH:mm",
            Locale.getDefault()
        )

        return claimsSnapshot.documents.mapNotNull { claimDocument ->
            val itemId = claimDocument.getString("itemId")
                ?: return@mapNotNull null

            val claimantUid = claimDocument.getString("claimantUid")
                ?: return@mapNotNull null

            val reportSnapshot = currentFirestore.collection("reports")
                .document(itemId)
                .get()
                .await()

            val userSnapshot = currentFirestore.collection("users")
                .document(claimantUid)
                .get()
                .await()

            val itemName = reportSnapshot.getString("itemName")
                ?: "Unknown item"

            val claimantName = userSnapshot.getString("fullName")
                ?: "Unknown claimant"

            val submittedTimestamp =
                claimDocument.getTimestamp("createdAt")

            val submittedDate = submittedTimestamp?.let {
                dateFormatter.format(it.toDate())
            } ?: "Date unavailable"

            AdminClaim(
                id = claimDocument.id,
                itemName = itemName,
                claimantName = claimantName,
                submittedDate = submittedDate,
                status = claimDocument.getString("status") ?: "PENDING",
                proofDescription = claimDocument.getString("proofDescription")
                    ?: "No ownership evidence was provided."
            )
        }
    }
    suspend fun decideClaim(
        claimId: String,
        approve: Boolean
    ) {
        val currentAuth = auth
            ?: throw IllegalStateException(
                "Firebase Authentication is not initialized."
            )

        val currentFirestore = firestore
            ?: throw IllegalStateException(
                "Firebase Firestore is not initialized."
            )

        val user = currentAuth.currentUser
            ?: throw IllegalStateException(
                "Please log in before reviewing claims."
            )

        require(claimId.isNotBlank()) {
            "The claim ID is missing."
        }

        // Verify the administrator's profile before attempting a decision.
        val profile = currentFirestore.collection("users")
            .document(user.uid)
            .get()
            .await()

        check(
            profile.getString("role") == "ADMIN" &&
                    profile.getString("accountStatus") == "ACTIVE"
        ) {
            "Only active administrators can review claims."
        }

        val claimRef = currentFirestore.collection("claims")
            .document(claimId)

        val newStatus = if (approve) "APPROVED" else "REJECTED"

        // A transaction prevents two reviewers from deciding the same
        // pending claim at the same time.
        currentFirestore.runTransaction { transaction ->
            val claim = transaction.get(claimRef)

            check(claim.exists()) {
                "This claim no longer exists."
            }

            check(claim.getString("status") == "PENDING") {
                "This claim has already been reviewed."
            }

            transaction.update(
                claimRef,
                mapOf(
                    "status" to newStatus,
                    "reviewedBy" to user.uid,
                    "reviewedAt" to FieldValue.serverTimestamp()
                )
            )

            true
        }.await()
    }
}
