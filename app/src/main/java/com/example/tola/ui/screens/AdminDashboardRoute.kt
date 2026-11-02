package com.example.tola.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import com.example.tola.auth.FirebaseAuthRepository
import com.example.tola.data.AdminDashboardRepository
import com.example.tola.data.AdminDashboardStats

@Composable
fun AdminDashboardRoute(
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

    var adminName by remember {
        mutableStateOf("Administrator")
    }

    var dashboardError by remember {
        mutableStateOf<String?>(null)
    }

    val isPreview = LocalInspectionMode.current

    LaunchedEffect(isPreview) {
        if (!isPreview) {
            val authRepo = FirebaseAuthRepository()
            val profile = authRepo.getCurrentUserProfile()
            if (profile != null && profile.fullName.isNotBlank()) {
                adminName = profile.fullName
            }
        }
    }

    DisposableEffect(isPreview) {
        if (isPreview) {
            onDispose { }
        } else {
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
    }

    AdminDashboardScreen(
        adminName = adminName,
        totalUsers = stats.totalUsers,
        lostReports = stats.lostReports,
        foundReports = stats.foundReports,
        pendingClaims = stats.pendingClaims,
        recoveredItems = stats.recoveredItems,
        dashboardError = dashboardError,
        onUsersClick = onUsersClick,
        onReportsClick = onReportsClick,
        onClaimsClick = onClaimsClick,
        onCategoriesClick = onCategoriesClick,
        onNotificationsClick = onNotificationsClick,
        onLogoutClick = onLogoutClick
    )
}
