package com.example.tola.data

import android.content.Context
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

    private fun getReadIds(context: Context): Set<String> {
        val prefs = context.getSharedPreferences("tola_notifications", Context.MODE_PRIVATE)
        return prefs.getStringSet("read_ids", emptySet()) ?: emptySet()
    }

    fun markAsRead(context: Context, notificationId: String) {
        val prefs = context.getSharedPreferences("tola_notifications", Context.MODE_PRIVATE)
        val readIds = prefs.getStringSet("read_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        readIds.add(notificationId)
        prefs.edit().putStringSet("read_ids", readIds).apply()
    }

    fun markAllAsRead(context: Context, notificationIds: List<String>) {
        val prefs = context.getSharedPreferences("tola_notifications", Context.MODE_PRIVATE)
        val readIds = prefs.getStringSet("read_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        readIds.addAll(notificationIds)
        prefs.edit().putStringSet("read_ids", readIds).apply()
    }

    suspend fun getStudentNotifications(context: Context? = null): List<StudentNotification> {
        val currentAuth = auth ?: return emptyList()
        val currentFirestore = firestore ?: return emptyList()
        val user = currentAuth.currentUser ?: return emptyList()

        val readIds = if (context != null) getReadIds(context) else emptySet()
        val notifications = mutableListOf<StudentNotification>()
        val userLostReports = mutableListOf<LostFoundReport>()

        try {
            // 1. Fetch User's Claims Updates
            val claimsSnapshot = currentFirestore.collection("claims")
                .whereEqualTo("claimantUid", user.uid)
                .get()
                .await()

            for (claimDoc in claimsSnapshot.documents) {
                val claimId = claimDoc.id
                val notifId = "claim_$claimId"
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

                val isRead = readIds.contains(notifId) || status != "PENDING"

                notifications.add(
                    StudentNotification(
                        id = notifId,
                        title = title,
                        message = message,
                        timeLabel = timeLabel,
                        type = type,
                        isRead = isRead,
                        itemId = itemId
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
                val notifId = "report_$reportId"
                val itemName = reportDoc.getString("itemName") ?: "Reported Item"
                val category = reportDoc.getString("category") ?: ""
                val status = reportDoc.getString("status") ?: "OPEN"
                val type = reportDoc.getString("type") ?: "LOST"
                val timestamp = reportDoc.getTimestamp("updatedAt")
                    ?: reportDoc.getTimestamp("createdAt")

                val date = timestamp?.toDate() ?: Date()
                val timeLabel = formatTimeAgo(date)

                if (type == "LOST" && status == "OPEN") {
                    userLostReports.add(
                        LostFoundReport(
                            id = reportId,
                            itemName = itemName,
                            category = category,
                            type = type,
                            status = status
                        )
                    )
                }

                val isRead = readIds.contains(notifId) || (status == "RECOVERED" || status == "RESOLVED")

                if (status == "RECOVERED" || status == "RESOLVED") {
                    notifications.add(
                        StudentNotification(
                            id = notifId,
                            title = "Item Marked as Recovered",
                            message = "Your $type report for '$itemName' was marked as recovered.",
                            timeLabel = timeLabel,
                            type = StudentNotificationType.RECOVERY,
                            isRead = isRead,
                            itemId = reportId
                        )
                    )
                } else {
                    notifications.add(
                        StudentNotification(
                            id = notifId,
                            title = "$type Report Active",
                            message = "Your $type report for '$itemName' is live on the campus feed.",
                            timeLabel = timeLabel,
                            type = if (type == "FOUND") StudentNotificationType.FOUND_ITEM else StudentNotificationType.ITEM_MATCH,
                            isRead = isRead,
                            itemId = reportId
                        )
                    )
                }
            }

            // 3. Fetch Reports Submitted by OTHER Users (Cross-User Notifications)
            val otherReportsSnapshot = currentFirestore.collection("reports")
                .whereNotEqualTo("ownerUid", user.uid)
                .get()
                .await()

            for (reportDoc in otherReportsSnapshot.documents) {
                val reportId = reportDoc.id
                val notifId = "other_report_$reportId"
                val itemName = reportDoc.getString("itemName") ?: "Found Item"
                val category = reportDoc.getString("category") ?: ""
                val location = reportDoc.getString("location") ?: "Campus"
                val type = reportDoc.getString("type") ?: "FOUND"
                val status = reportDoc.getString("status") ?: "OPEN"
                val timestamp = reportDoc.getTimestamp("createdAt")

                if (status != "OPEN") continue

                val date = timestamp?.toDate() ?: Date()
                val timeLabel = formatTimeAgo(date)
                val isRead = readIds.contains(notifId)

                val matchingLostReport = userLostReports.firstOrNull { lost ->
                    (category.isNotBlank() && lost.category.equals(category, ignoreCase = true)) ||
                            itemName.contains(lost.itemName, ignoreCase = true) ||
                            lost.itemName.contains(itemName, ignoreCase = true)
                }

                if (type == "FOUND" && matchingLostReport != null) {
                    notifications.add(
                        StudentNotification(
                            id = notifId,
                            title = "Possible Item Match Found! 🔍",
                            message = "A found '$itemName' matching your lost '${matchingLostReport.itemName}' was reported at $location.",
                            timeLabel = timeLabel,
                            type = StudentNotificationType.ITEM_MATCH,
                            isRead = isRead,
                            itemId = reportId
                        )
                    )
                } else if (type == "FOUND") {
                    notifications.add(
                        StudentNotification(
                            id = notifId,
                            title = "New Found Item Reported",
                            message = "'$itemName' was reported found at $location.",
                            timeLabel = timeLabel,
                            type = StudentNotificationType.FOUND_ITEM,
                            isRead = isRead,
                            itemId = reportId
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
