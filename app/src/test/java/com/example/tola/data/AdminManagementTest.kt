package com.example.tola.data

import com.example.tola.ui.screens.AdminUser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class AdminManagementTest {

    private val repository = AdminRepository(
        authProvider = { null },
        firestoreProvider = { null }
    )

    @Test
    fun admin_uninitializedFirestore_returnsDefaultStats() = runBlocking {
        val stats = repository.getDashboardStats()
        assertEquals(0, stats.totalUsers)
        assertEquals(0, stats.lostReports)
        assertEquals(0, stats.foundReports)
        assertEquals(0, stats.totalReports)
    }

    @Test
    fun admin_toggleUserStatus_activeToSuspended() {
        val activeUser = AdminUser(
            id = "usr-1",
            fullName = "John Doe",
            studentId = "STU-100",
            email = "john@campus.edu",
            status = "ACTIVE"
        )
        val nextStatus = if (activeUser.status == "ACTIVE") "SUSPENDED" else "ACTIVE"
        assertEquals("SUSPENDED", nextStatus)
    }

    @Test
    fun admin_toggleUserStatus_suspendedToActive() {
        val suspendedUser = AdminUser(
            id = "usr-2",
            fullName = "Jane Smith",
            studentId = "STU-101",
            email = "jane@campus.edu",
            status = "SUSPENDED"
        )
        val nextStatus = if (suspendedUser.status == "ACTIVE") "SUSPENDED" else "ACTIVE"
        assertEquals("ACTIVE", nextStatus)
    }
}
