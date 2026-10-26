package com.example.tola.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaTextPrimary
import com.example.tola.ui.theme.TolaTextSecondary
import com.example.tola.ui.theme.TolaTheme

@Composable
fun AdminDashboardScreen(
    adminName: String = "Administrator",
    totalUsers: Int = 0,
    lostReports: Int = 0,
    foundReports: Int = 0,
    pendingClaims: Int = 0,
    recoveredItems: Int = 0,
    dashboardError: String? = null,
    onUsersClick: () -> Unit = {},
    onReportsClick: () -> Unit = {},
    onClaimsClick: () -> Unit = {},
    onCategoriesClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    AdminScreenLayout(
        title = "Dashboard",
        subtitle = "Welcome, $adminName. Here's the current overview.",
    ) {
        Text("Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TolaTextPrimary)
        if (dashboardError != null) {
            Text(
                text = dashboardError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminStatCard("Total users", totalUsers, Modifier.weight(1f))
            AdminStatCard("Lost reports", lostReports, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminStatCard("Found reports", foundReports, Modifier.weight(1f))
            AdminStatCard("Pending claims", pendingClaims, Modifier.weight(1f))
        }
        AdminStatCard("Recovered items", recoveredItems, Modifier.fillMaxWidth())

        Text("Management", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TolaTextPrimary)
        AdminDashboardAction("User Management", "View and manage registered accounts.", "Manage users", onUsersClick)
        AdminDashboardAction("Report Management", "Review lost and found item reports.", "Manage reports", onReportsClick)
        AdminDashboardAction("Claim Review", "Review claims and ownership evidence.", "Review claims", onClaimsClick)
        AdminDashboardAction("Category Management", "Maintain the categories used in reports.", "Manage categories", onCategoriesClick)
        AdminDashboardAction("Notifications", "Review system updates and alerts.", "View notifications", onNotificationsClick)

        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Logout", color = TolaPrimary, fontWeight = FontWeight.SemiBold) }
    }
}

@Composable
private fun AdminStatCard(title: String, value: Int, modifier: Modifier = Modifier) {
    AdminPanelCard {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
        Text(value.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TolaPrimary)
    }
}

@Composable
private fun AdminDashboardAction(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit
) {
    AdminPanelCard {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TolaTextPrimary)
        Text(description, style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TolaPrimary,
                contentColor = Color.White
            )
        ) { Text(buttonText, fontWeight = FontWeight.Bold, color = Color.White) }
    }
}

@Preview(name = "Tola Admin Dashboard", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AdminDashboardScreenPreview() {
    TolaTheme {
        AdminDashboardScreen(
            adminName = "Tola Admin",
            totalUsers = 120,
            lostReports = 18,
            foundReports = 12,
            pendingClaims = 4,
            recoveredItems = 25
        )
    }
}
