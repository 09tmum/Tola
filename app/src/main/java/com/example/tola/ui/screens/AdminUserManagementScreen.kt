package com.example.tola.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tola.lostfound.ui.theme.*

@Composable
fun AdminUserManagementScreen(
    users: List<AdminUser> = emptyList(),
    onBack: () -> Unit = {},
    onViewUser: (AdminUser) -> Unit = {},
    onToggleUserStatus: (AdminUser) -> Unit = {}
) {
    var query by rememberSaveable { mutableStateOf("") }
    val visibleUsers = users.filter {
        it.name.contains(query, ignoreCase = true) ||
            it.email.contains(query, ignoreCase = true) ||
            it.id.contains(query, ignoreCase = true)
    }

    AdminScreenLayout("User Management", "Search accounts and manage account status.", onBack) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search users") },
            placeholder = { Text("Name, email or user ID") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TolaPrimary,
                unfocusedBorderColor = TolaBorderStrong,
                focusedLabelColor = TolaPrimary,
                cursorColor = TolaPrimary
            )
        )
        Text("${visibleUsers.size} user(s)", style = MaterialTheme.typography.labelLarge, color = TolaTextSecondary)
        if (visibleUsers.isEmpty()) {
            AdminEmptyState(if (users.isEmpty()) "No user records are connected yet." else "No users match your search.")
        } else {
            visibleUsers.forEach { user ->
                AdminPanelCard {
                    Text(user.name, style = MaterialTheme.typography.titleMedium, color = TolaTextPrimary)
                    Text(user.email, style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
                    Text("ID: ${user.id} · Role: ${user.role}", style = MaterialTheme.typography.bodySmall, color = TolaTextMuted)
                    AdminStatusChip(
                        label = if (user.isActive) "ACTIVE" else "SUSPENDED",
                        background = if (user.isActive) TolaPrimaryContainer else TolaSurfaceVariant,
                        foreground = if (user.isActive) TolaOnPrimaryContainer else TolaTextSecondary
                    )
                    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onViewUser(user) },
                            shape = RoundedCornerShape(10.dp)
                        ) { Text("View details", color = TolaPrimary) }
                        Button(
                            onClick = { onToggleUserStatus(user) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TolaPrimary)
                        ) { Text(if (user.isActive) "Suspend" else "Activate") }
                    }
                }
            }
        }
    }
}

@Preview(name = "Admin User Management", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AdminUserManagementPreview() {
    TolaTheme { AdminUserManagementScreen(users = previewAdminUsers) }
}
