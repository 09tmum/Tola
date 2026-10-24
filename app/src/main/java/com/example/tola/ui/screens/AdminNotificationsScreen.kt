package com.example.tola.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tola.lostfound.ui.theme.*

@Composable
fun AdminNotificationsScreen(
    notifications: List<AdminNotification> = emptyList(),
    onBack: () -> Unit = {},
    onOpenNotification: (AdminNotification) -> Unit = {},
    onMarkRead: (AdminNotification) -> Unit = {}
) {
    AdminScreenLayout("Notifications", "Review important activity and system updates.", onBack) {
        if (notifications.isEmpty()) {
            AdminEmptyState("There are no notifications to display.")
        } else {
            notifications.forEach { notification ->
                AdminPanelCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(notification.title, style = MaterialTheme.typography.titleMedium, color = TolaTextPrimary, modifier = Modifier.weight(1f))
                        AdminStatusChip(
                            label = if (notification.isRead) "READ" else "NEW",
                            background = if (notification.isRead) TolaSurfaceVariant else TolaPrimaryContainer,
                            foreground = if (notification.isRead) TolaTextSecondary else TolaOnPrimaryContainer
                        )
                    }
                    Text(notification.message, style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
                    Text(notification.timeLabel, style = MaterialTheme.typography.bodySmall, color = TolaTextMuted)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { onOpenNotification(notification) }) {
                            Text("Open", color = TolaPrimary)
                        }
                        if (!notification.isRead) {
                            OutlinedButton(onClick = { onMarkRead(notification) }) {
                                Text("Mark read", color = TolaPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Admin Notifications", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AdminNotificationsPreview() {
    TolaTheme { AdminNotificationsScreen(notifications = previewAdminNotifications) }
}
