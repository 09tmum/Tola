package com.example.tola.data

import com.example.tola.ui.screens.AdminCategory
import com.example.tola.ui.screens.AdminClaim
import com.example.tola.ui.screens.AdminItemReport
import com.example.tola.ui.screens.AdminNotification
import com.example.tola.ui.screens.AdminUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AdminRepository(
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

    suspend fun getDashboardStats(): AdminDashboardStats {
        val currentAuth = auth ?: return AdminDashboardStats()
        val currentFirestore = firestore ?: return AdminDashboardStats()
        val currentUser = currentAuth.currentUser ?: return AdminDashboardStats()

        val adminName = try {
            val userDoc = currentFirestore.collection("users")
                .document(currentUser.uid)
                .get()
                .await()
            userDoc.getString("fullName")?.ifBlank { "Administrator" } ?: "Administrator"
        } catch (_: Exception) {
            "Administrator"
        }

        val totalUsers = try {
            val snapshot = currentFirestore.collection("users").get().await()
            snapshot.size()
        } catch (_: Exception) { 0 }

        val reportsSnapshot = try {
            currentFirestore.collection("reports").get().await()
        } catch (_: Exception) { null }

        val lostReports = reportsSnapshot?.documents?.count {
            it.getString("type") == "LOST"
        } ?: 0

        val foundReports = reportsSnapshot?.documents?.count {
            it.getString("type") == "FOUND"
        } ?: 0

        val recoveredItems = reportsSnapshot?.documents?.count {
            val status = it.getString("status")
            status == "RESOLVED" || status == "RECOVERED" || status == "RETURNED" || status == "CLOSED"
        } ?: 0

        val pendingClaims = try {
            val claimsSnapshot = currentFirestore.collection("claims")
                .whereEqualTo("status", "PENDING")
                .get()
                .await()
            claimsSnapshot.size()
        } catch (_: Exception) { 0 }

        return AdminDashboardStats(
            adminName = adminName,
            totalUsers = totalUsers,
            lostReports = lostReports,
            foundReports = foundReports,
            pendingClaims = pendingClaims,
            recoveredItems = recoveredItems
        )
    }

    suspend fun getUsers(): List<AdminUser> {
        val currentFirestore = firestore ?: return emptyList()
        return try {
            val snapshot = currentFirestore.collection("users").get().await()
            snapshot.documents.map { doc ->
                AdminUser(
                    id = doc.id,
                    name = doc.getString("fullName") ?: "Campus User",
                    email = doc.getString("email") ?: "",
                    role = doc.getString("role") ?: "STUDENT",
                    isActive = doc.getString("accountStatus") != "SUSPENDED"
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun toggleUserStatus(userId: String, currentIsActive: Boolean) {
        val currentFirestore = firestore
            ?: throw IllegalStateException("Firebase Firestore is not initialized.")

        val newStatus = if (currentIsActive) "SUSPENDED" else "ACTIVE"
        currentFirestore.collection("users")
            .document(userId)
            .update("accountStatus", newStatus)
            .await()
    }

    suspend fun getReports(): List<AdminItemReport> {
        val currentFirestore = firestore ?: return emptyList()
        return try {
            val snapshot = currentFirestore.collection("reports").get().await()
            snapshot.documents.map { doc ->
                val typeRaw = doc.getString("type") ?: "LOST"
                val statusRaw = doc.getString("status") ?: "OPEN"
                AdminItemReport(
                    id = doc.id,
                    itemName = doc.getString("itemName") ?: "Reported Item",
                    reportType = if (typeRaw.equals("FOUND", true)) "Found" else "Lost",
                    reporterName = doc.getString("ownerUid")?.takeLast(6) ?: "User",
                    location = doc.getString("location") ?: "Campus",
                    status = if (statusRaw.equals("OPEN", true)) "Open" else statusRaw
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun markReportRecovered(reportId: String) {
        val currentFirestore = firestore
            ?: throw IllegalStateException("Firebase Firestore is not initialized.")

        currentFirestore.collection("reports")
            .document(reportId)
            .update(
                mapOf(
                    "status" to "RECOVERED",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            .await()
    }

    suspend fun removeReport(reportId: String) {
        val currentFirestore = firestore
            ?: throw IllegalStateException("Firebase Firestore is not initialized.")

        currentFirestore.collection("reports")
            .document(reportId)
            .delete()
            .await()
    }

    suspend fun getClaims(): List<AdminClaim> {
        val currentFirestore = firestore ?: return emptyList()
        return try {
            val snapshot = currentFirestore.collection("claims").get().await()
            snapshot.documents.map { doc ->
                val statusRaw = doc.getString("status") ?: "PENDING"
                AdminClaim(
                    id = doc.id,
                    itemName = doc.getString("itemId") ?: "Item Claim",
                    claimantName = doc.getString("claimantUid")?.takeLast(6) ?: "Claimant",
                    submittedDate = doc.getString("createdAt") ?: "Recently",
                    status = if (statusRaw.equals("PENDING", true)) "Pending" else statusRaw,
                    proofDescription = doc.getString("proofDescription") ?: ""
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getCategories(): List<AdminCategory> {
        val currentFirestore = firestore ?: return emptyList()
        return try {
            val snapshot = currentFirestore.collection("categories").get().await()
            if (snapshot.isEmpty) {
                listOf(
                    AdminCategory("CAT01", "Electronics", 12),
                    AdminCategory("CAT02", "Bags & Backpacks", 8),
                    AdminCategory("CAT03", "IDs & Documents", 15),
                    AdminCategory("CAT04", "Books & Notes", 6),
                    AdminCategory("CAT05", "Accessories", 10)
                )
            } else {
                snapshot.documents.map { doc ->
                    AdminCategory(
                        id = doc.id,
                        name = doc.getString("name") ?: "Category",
                        itemCount = doc.getLong("itemCount")?.toInt() ?: 0
                    )
                }
            }
        } catch (_: Exception) {
            listOf(
                AdminCategory("CAT01", "Electronics", 12),
                AdminCategory("CAT02", "Bags & Backpacks", 8),
                AdminCategory("CAT03", "IDs & Documents", 15),
                AdminCategory("CAT04", "Books & Notes", 6),
                AdminCategory("CAT05", "Accessories", 10)
            )
        }
    }

    suspend fun addCategory(categoryName: String) {
        val currentFirestore = firestore
            ?: throw IllegalStateException("Firebase Firestore is not initialized.")

        val data = hashMapOf(
            "name" to categoryName.trim(),
            "itemCount" to 0,
            "createdAt" to FieldValue.serverTimestamp()
        )
        currentFirestore.collection("categories")
            .add(data)
            .await()
    }

    suspend fun removeCategory(categoryId: String) {
        val currentFirestore = firestore
            ?: throw IllegalStateException("Firebase Firestore is not initialized.")

        currentFirestore.collection("categories")
            .document(categoryId)
            .delete()
            .await()
    }

    suspend fun getNotifications(): List<AdminNotification> {
        val currentFirestore = firestore ?: return emptyList()
        return try {
            val notifSnapshot = currentFirestore.collection("admin_notifications").get().await()
            if (!notifSnapshot.isEmpty) {
                return notifSnapshot.documents.map { doc ->
                    AdminNotification(
                        id = doc.id,
                        title = doc.getString("title") ?: "System Alert",
                        message = doc.getString("message") ?: "",
                        timeLabel = doc.getString("timeLabel") ?: "Recently",
                        isRead = doc.getBoolean("isRead") == true
                    )
                }
            }

            val liveNotifications = mutableListOf<AdminNotification>()

            val pendingClaims = currentFirestore.collection("claims")
                .whereEqualTo("status", "PENDING")
                .get()
                .await()

            pendingClaims.documents.forEach { doc ->
                liveNotifications.add(
                    AdminNotification(
                        id = doc.id,
                        title = "Pending ownership claim",
                        message = "A claim is waiting for review on item #${doc.getString("itemId")?.takeLast(6) ?: "Report"}.",
                        timeLabel = "Action required",
                        isRead = false
                    )
                )
            }

            val recentReports = currentFirestore.collection("reports")
                .whereEqualTo("status", "OPEN")
                .get()
                .await()

            recentReports.documents.take(5).forEach { doc ->
                val type = doc.getString("type") ?: "LOST"
                val itemName = doc.getString("itemName") ?: "Item"
                liveNotifications.add(
                    AdminNotification(
                        id = doc.id,
                        title = "New $type report",
                        message = "$itemName was reported at ${doc.getString("location") ?: "campus"}.",
                        timeLabel = doc.getString("date") ?: "Recently",
                        isRead = true
                    )
                )
            }

            liveNotifications
        } catch (_: Exception) {
            emptyList()
        }
    }
}
