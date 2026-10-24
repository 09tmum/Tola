package com.tola.lostfound.ui.screens

/** UI models for the Tola admin screens. Replace sample values with repository data. */
data class AdminUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String = "Student",
    val isActive: Boolean = true
)

data class AdminItemReport(
    val id: String,
    val itemName: String,
    val reportType: String, // Expected: "Lost" or "Found"
    val reporterName: String,
    val location: String,
    val status: String = "Open" // e.g. Open, Under Review, Recovered
)

data class AdminClaim(
    val id: String,
    val itemName: String,
    val claimantName: String,
    val submittedDate: String,
    val status: String = "Pending"
)

data class AdminCategory(
    val id: String,
    val name: String,
    val itemCount: Int = 0
)

data class AdminNotification(
    val id: String,
    val title: String,
    val message: String,
    val timeLabel: String,
    val isRead: Boolean = false
)

internal val previewAdminUsers = listOf(
    AdminUser("U001", "Chisomo Banda", "chisomo@example.com"),
    AdminUser("U002", "Thoko Phiri", "thoko@example.com", isActive = false),
    AdminUser("U003", "Peter Zulu", "peter@example.com")
)

internal val previewAdminReports = listOf(
    AdminItemReport("R001", "Student ID Card", "Lost", "Chisomo Banda", "Library", "Open"),
    AdminItemReport("R002", "Black Backpack", "Found", "Thoko Phiri", "Lecture Theatre", "Under Review"),
    AdminItemReport("R003", "Set of Keys", "Found", "Peter Zulu", "Cafeteria", "Recovered")
)

internal val previewAdminClaims = listOf(
    AdminClaim("C001", "Student ID Card", "Chisomo Banda", "24 Oct 2026"),
    AdminClaim("C002", "Black Backpack", "Peter Zulu", "23 Oct 2026")
)

internal val previewAdminCategories = listOf(
    AdminCategory("CAT001", "Identification Cards", 8),
    AdminCategory("CAT002", "Books & Notes", 12),
    AdminCategory("CAT003", "Electronics", 5),
    AdminCategory("CAT004", "Keys & Accessories", 7)
)

internal val previewAdminNotifications = listOf(
    AdminNotification("N001", "New ownership claim", "A claim was submitted for Student ID Card.", "5 minutes ago"),
    AdminNotification("N002", "Report needs review", "A found-item report may be a duplicate.", "Yesterday", true)
)
