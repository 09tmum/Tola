
package com.example.tola.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.tola.data.AdminDashboardRepository
import com.example.tola.data.AdminDashboardStats

@Composable
fun AdminDashboardRoute(
    adminName: String = "Administrator",
    onUsersClick: () -> Unit = {},
    onReportsClick: () -> Unit = {},
    onClaimsClick: () -> Unit = {},
    onCategoriesClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var stats by remember {
        mutableStateOf(AdminDashboardStats())
    }

    var dashboardError by remember {
        mutableStateOf<String?>(null)
    }

    DisposableEffect(Unit) {
        val repository = AdminDashboardRepository()

        val listeners = repository.listenToStats(
            onStatsChanged = { newStats ->
                stats = newStats
                dashboardError = null
            },
            onError = { message ->
                dashboardError = message
                Log.e("AdminDashboard", message)
            }
        )

        onDispose {
            listeners.forEach { it.remove() }
        }
    }

    AdminDashboardScreen(
        adminName = adminName,
        totalUsers = stats.totalUsers,
        lostReports = stats.lostReports,
        foundReports = stats.foundReports,
        dashboardError = dashboardError,
        onUsersClick = onUsersClick,
        onReportsClick = onReportsClick,
        onClaimsClick = onClaimsClick,
        onCategoriesClick = onCategoriesClick,
        onNotificationsClick = onNotificationsClick,
        onLogoutClick = onLogoutClick
    )
}
