package com.example.tola.data

import com.example.tola.ui.screens.StudentNotification
import com.example.tola.ui.screens.StudentNotificationType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificationRepository(
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

    suspend fun getStudentNotifications(): List<StudentNotification> {
        val currentAuth = auth ?: return emptyList()
        val currentFirestore = firestore ?: return emptyList()
        val user = currentAuth.currentUser ?: return emptyList()

        val notifications = mutableListOf<StudentNotification>()

        try {
            // 1. Fetch User's Claims Updates
            val claimsSnapshot = currentFirestore.collection("claims")
                .whereEqualTo("claimantUid", user.uid)
                .get()
                .await()

            for (claimDoc in claimsSnapshot.documents) {
                val claimId = claimDoc.id
                val status = claimDoc.getString("status") ?: "PENDING"
                val itemId = claimDoc.getString("itemId") ?: ""
                val timestamp = claimDoc.getTimestamp("reviewedAt")
                    ?: claimDoc.getTimestamp("createdAt")

                val date = timestamp?.toDate() ?: Date()
                val timeLabel = formatTimeAgo(date)

                val itemTitle = if (itemId.isNotBlank()) {
                    try {
                        val itemDoc = currentFirestore.collection("reports").document(itemId).get().await()
                        itemDoc.getString("itemName") ?: "Found Item"
                    } catch (_: Exception) { "Found Item" }
                } else "Found Item"

                val (title, message, type) = when (status.uppercase()) {
                    "APPROVED" -> Triple(
                        "Claim Approved! 🎉",
                        "Your ownership claim for '$itemTitle' has been approved by Campus Admin. Visit support to collect.",
                        StudentNotificationType.RECOVERY
                    )
                    "REJECTED" -> Triple(
                        "Claim Decision Update",
                        "Your ownership claim for '$itemTitle' was reviewed and rejected. Contact support for details.",
                        StudentNotificationType.CLAIM
                    )
                    else -> Triple(
                        "Claim Under Review",
                        "Your ownership claim for '$itemTitle' was received and is currently being reviewed.",
                        StudentNotificationType.CLAIM
                    )
                }

                notifications.add(
                    StudentNotification(
                        id = "claim_$claimId",
                        title = title,
                        message = message,
                        timeLabel = timeLabel,
                        type = type,
                        isRead = status != "PENDING"
                    )
                )
            }

            // 2. Fetch User's Item Report Status Updates
            val reportsSnapshot = currentFirestore.collection("reports")
                .whereEqualTo("ownerUid", user.uid)
                .get()
                .await()

            for (reportDoc in reportsSnapshot.documents) {
                val reportId = reportDoc.id
                val itemName = reportDoc.getString("itemName") ?: "Reported Item"
                val status = reportDoc.getString("status") ?: "OPEN"
                val type = reportDoc.getString("type") ?: "LOST"
                val timestamp = reportDoc.getTimestamp("updatedAt")
                    ?: reportDoc.getTimestamp("createdAt")

                val date = timestamp?.toDate() ?: Date()
                val timeLabel = formatTimeAgo(date)

                if (status == "RECOVERED" || status == "RESOLVED") {
                    notifications.add(
                        StudentNotification(
                            id = "report_$reportId",
                            title = "Item Marked as Recovered",
                            message = "Your $type report for '$itemName' was marked as recovered.",
                            timeLabel = timeLabel,
                            type = StudentNotificationType.RECOVERY,
                            isRead = true
                        )
                    )
                } else {
                    notifications.add(
                        StudentNotification(
                            id = "report_$reportId",
                            title = "$type Report Active",
                            message = "Your $type report for '$itemName' is live on the campus feed.",
                            timeLabel = timeLabel,
                            type = if (type == "FOUND") StudentNotificationType.FOUND_ITEM else StudentNotificationType.ITEM_MATCH,
                            isRead = true
                        )
                    )
                }
            }
        } catch (_: Exception) {
            // Return collected notifications if partial query succeeds
        }

        return notifications
    }

    companion object {
        fun formatTimeAgo(date: Date): String {
            val diffMs = System.currentTimeMillis() - date.time
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
            val hours = TimeUnit.MILLISECONDS.toHours(diffMs)
            val days = TimeUnit.MILLISECONDS.toDays(diffMs)

            return when {
                diffMs < 0 || minutes < 1 -> "Just now"
                minutes < 60 -> "$minutes min ago"
                hours < 24 -> "$hours hour${if (hours > 1) "s" else ""} ago"
                days < 7 -> "$days day${if (days > 1) "s" else ""} ago"
                else -> {
                    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    formatter.format(date)
                }
            }
        }
    }
}
