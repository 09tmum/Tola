package com.example.tola.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

data class AdminDashboardStats(
    val adminName: String = "Administrator",
    val totalUsers: Int = 0,
    val lostReports: Int = 0,
    val foundReports: Int = 0,
    val pendingClaims: Int = 0,
    val recoveredItems: Int = 0,
    val totalReports: Int = lostReports + foundReports
)

class AdminDashboardRepository(
    private val firestoreProvider: () -> FirebaseFirestore? = {
        try { FirebaseFirestore.getInstance() } catch (_: Exception) { null }
    }
) {
    private val firestore: FirebaseFirestore?
        get() = firestoreProvider()

    fun listenToStats(
        onStatsChanged: (AdminDashboardStats) -> Unit,
        onError: (String) -> Unit
    ): List<ListenerRegistration> {
        val currentFirestore = firestore ?: run {
            onError("Firestore is not initialized.")
            return emptyList()
        }

        var usersLoaded = false
        var reportsLoaded = false
        var claimsLoaded = false

        var totalUsers = 0
        var lostReports = 0
        var foundReports = 0
        var pendingClaims = 0
        var recoveredItems = 0

        fun publishIfReady() {
            if (usersLoaded && reportsLoaded && claimsLoaded) {
                onStatsChanged(
                    AdminDashboardStats(
                        totalUsers = totalUsers,
                        lostReports = lostReports,
                        foundReports = foundReports,
                        pendingClaims = pendingClaims,
                        recoveredItems = recoveredItems
                    )
                )
            }
        }

        val usersListener = currentFirestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError("Could not load users: ${error.localizedMessage}")
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    onError("No user data was returned.")
                    return@addSnapshotListener
                }

                totalUsers = snapshot.documents.count { document ->
                    document.getString("role")
                        ?.equals("ADMIN", ignoreCase = true) != true
                }

                usersLoaded = true
                publishIfReady()
            }

        val reportsListener = currentFirestore.collection("reports")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError("Could not load reports: ${error.localizedMessage}")
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    onError("No report data was returned.")
                    return@addSnapshotListener
                }

                lostReports = snapshot.documents.count { document ->
                    document.getString("type")
                        ?.equals("LOST", ignoreCase = true) == true
                }

                foundReports = snapshot.documents.count { document ->
                    document.getString("type")
                        ?.equals("FOUND", ignoreCase = true) == true
                }

                recoveredItems = snapshot.documents.count { document ->
                    val status = document.getString("status")
                    status?.equals("RESOLVED", ignoreCase = true) == true ||
                            status?.equals("RECOVERED", ignoreCase = true) == true ||
                            status?.equals("RETURNED", ignoreCase = true) == true
                }

                reportsLoaded = true
                publishIfReady()
            }

        val claimsListener = currentFirestore.collection("claims")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    claimsLoaded = true
                    publishIfReady()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    pendingClaims = snapshot.documents.count { document ->
                        document.getString("status")
                            ?.equals("PENDING", ignoreCase = true) == true
                    }
                }

                claimsLoaded = true
                publishIfReady()
            }

        return listOf(usersListener, reportsListener, claimsListener)
    }
}
