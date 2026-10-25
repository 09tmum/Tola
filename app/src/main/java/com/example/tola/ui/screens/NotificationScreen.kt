package com.example.tola.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tola.ui.theme.TolaBackground
import com.example.tola.ui.theme.TolaBorder
import com.example.tola.ui.theme.TolaOnPrimaryContainer
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaPrimaryContainer
import com.example.tola.ui.theme.TolaSurface
import com.example.tola.ui.theme.TolaSurfaceVariant
import com.example.tola.ui.theme.TolaTextMuted
import com.example.tola.ui.theme.TolaTextPrimary
import com.example.tola.ui.theme.TolaTextSecondary
import com.example.tola.ui.theme.TolaTheme

enum class StudentNotificationType {
    CLAIM,
    ITEM_MATCH,
    FOUND_ITEM,
    RECOVERY,
    GENERAL
}

data class StudentNotification(
    val id: String,
    val title: String,
    val message: String,
    val timeLabel: String,
    val type: StudentNotificationType,
    val isRead: Boolean = false
)

@Composable
fun StudentNotificationsScreen(
    onBack: () -> Unit,
    notifications: List<StudentNotification> = emptyList(),
    onNotificationClick: (StudentNotification) -> Unit = {},
    onMarkAsRead: (String) -> Unit = {},
    onMarkAllAsRead: () -> Unit = {}
) {
    var selectedFilter by rememberSaveable {
        mutableStateOf("All")
    }

    val unreadCount = notifications.count { !it.isRead }

    val visibleNotifications = when (selectedFilter) {
        "Unread" -> notifications.filter { !it.isRead }
        "Read" -> notifications.filter { it.isRead }
        else -> notifications
    }

    Scaffold(
        containerColor = TolaBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TolaTextPrimary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TolaTextPrimary
                    )

                    Text(
                        text = if (unreadCount == 0) {
                            "You're all caught up"
                        } else {
                            "$unreadCount unread notification(s)"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TolaTextSecondary
                    )
                }

                if (unreadCount > 0) {
                    TextButton(onClick = onMarkAllAsRead) {
                        Text(
                            text = "Mark all read",
                            color = TolaPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Unread", "Read").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TolaPrimaryContainer,
                            selectedLabelColor = TolaOnPrimaryContainer,
                            containerColor = TolaSurface,
                            labelColor = TolaTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = TolaBorder,
                            selectedBorderColor = TolaPrimary,
                            enabled = true,
                            selected = selectedFilter == filter
                        )
                    )
                }
            }

            if (visibleNotifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(TolaPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = TolaPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = when (selectedFilter) {
                                "Unread" -> "No unread notifications"
                                "Read" -> "No read notifications"
                                else -> "No notifications yet"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TolaTextPrimary
                        )

                        Text(
                            text = "Updates about your items and claims will appear here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TolaTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(
                        items = visibleNotifications,
                        key = { it.id }
                    ) { notification ->
                        StudentNotificationCard(
                            notification = notification,
                            onClick = {
                                onNotificationClick(notification)
                            },
                            onMarkAsRead = {
                                onMarkAsRead(notification.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentNotificationCard(
    notification: StudentNotification,
    onClick: () -> Unit,
    onMarkAsRead: () -> Unit
) {
    val icon: ImageVector = when (notification.type) {
        StudentNotificationType.CLAIM ->
            Icons.Default.CheckCircle

        StudentNotificationType.ITEM_MATCH ->
            Icons.Default.Search

        StudentNotificationType.FOUND_ITEM ->
            Icons.Default.Notifications

        StudentNotificationType.RECOVERY ->
            Icons.Default.CheckCircle

        StudentNotificationType.GENERAL ->
            Icons.Default.Info
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (!notification.isRead) TolaPrimary.copy(alpha = 0.4f) else TolaBorder
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) TolaPrimaryContainer.copy(alpha = 0.3f) else TolaSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (!notification.isRead) TolaPrimaryContainer else TolaSurfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TolaPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TolaTextPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    if (!notification.isRead) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = TolaPrimary
                        ) {
                            Text(
                                text = "NEW",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TolaTextSecondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.timeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = TolaTextMuted
                    )

                    if (!notification.isRead) {
                        TextButton(
                            onClick = onMarkAsRead,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "Mark read",
                                color = TolaPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Student Notifications")
@Composable
private fun StudentNotificationsPreview() {
    TolaTheme {
        StudentNotificationsScreen(
            onBack = {},
            notifications = listOf(
                StudentNotification(
                    id = "1",
                    title = "Claim update",
                    message = "Your ownership claim is being reviewed.",
                    timeLabel = "10 minutes ago",
                    type = StudentNotificationType.CLAIM
                ),
                StudentNotification(
                    id = "2",
                    title = "Possible item match",
                    message = "A report may match an item you marked as lost.",
                    timeLabel = "1 hour ago",
                    type = StudentNotificationType.ITEM_MATCH,
                    isRead = true
                ),
                StudentNotification(
                    id = "3",
                    title = "Item recovery confirmed",
                    message = "Your item has been marked as recovered.",
                    timeLabel = "Yesterday",
                    type = StudentNotificationType.RECOVERY
                )
            )
        )
    }
}
