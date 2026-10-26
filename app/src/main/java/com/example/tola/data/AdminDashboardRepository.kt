package com.example.tola.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

data class AdminDashboardStats(
    val totalUsers: Int = 0,
    val lostReports: Int = 0,
    val foundReports: Int = 0
)

class AdminDashboardRepository(
    private val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
) {

    fun listenToStats(
        onStatsChanged: (AdminDashboardStats) -> Unit,
        onError: (String) -> Unit
    ): List<ListenerRegistration> {

        var usersLoaded = false
        var reportsLoaded = false

        var totalUsers = 0
        var lostReports = 0
        var foundReports = 0

        fun publishIfReady() {
            if (usersLoaded && reportsLoaded) {
                onStatsChanged(
                    AdminDashboardStats(
                        totalUsers = totalUsers,
                        lostReports = lostReports,
                        foundReports = foundReports
                    )
                )
            }
        }

        val usersListener = firestore.collection("users")
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

        val reportsListener = firestore.collection("reports")
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

                reportsLoaded = true
                publishIfReady()
            }

        return listOf(usersListener, reportsListener)
    }
}
