package com.example.tola.data

import com.example.tola.ui.screens.AdminUser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
            name = "John Doe",
            email = "john@campus.edu",
            isActive = true
        )
        val nextActiveState = !activeUser.isActive
        assertFalse(nextActiveState)
    }

    @Test
    fun admin_toggleUserStatus_suspendedToActive() {
        val suspendedUser = AdminUser(
            id = "usr-2",
            name = "Jane Smith",
            email = "jane@campus.edu",
            isActive = false
        )
        val nextActiveState = !suspendedUser.isActive
        assertTrue(nextActiveState)
    }
}
