package com.example.tola.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tola.ui.theme.TolaFoundTag
import com.example.tola.ui.theme.TolaLostOrange
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaPrimaryContainer

data class LostFoundFeedItem(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val timeAgo: String,
    val isFound: Boolean,
    val tagNumber: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onReportLost: () -> Unit,
    onReportFound: () -> Unit,
    onItemClick: (String) -> Unit,
    onProfileClick: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All Posts") }

    val items = remember {
        listOf(
            LostFoundFeedItem(
                id = "LF-1",
                title = "Cobalt Blue Hydro Flask",
                description = "Blue water bottle with a black lid.",
                location = "Main Library",
                timeAgo = "15 min ago",
                isFound = true,
                tagNumber = "LF-1024"
            ),
            LostFoundFeedItem(
                id = "LF-2",
                title = "North Face Recon Backpack",
                description = "Black backpack with a small red keychain.",
                location = "Computer Lab",
                timeAgo = "1 hour ago",
                isFound = false,
                tagNumber = "LF-1023"
            ),
            LostFoundFeedItem(
                id = "LF-3",
                title = "AirPods Pro with Red Case",
                description = "White AirPods in a red protective case.",
                location = "Student Centre",
                timeAgo = "2 hours ago",
                isFound = true,
                tagNumber = "LF-1022"
            )
        )
    }

    val filteredItems = when (selectedFilter) {
        "Lost Only" -> items.filter { !it.isFound }
        "Found Only" -> items.filter { it.isFound }
        else -> items
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 20.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Tola", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TolaPrimary)

                    Text(text = "Home", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsNone, contentDescription = "Notifications"
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TolaPrimary)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "AC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Greeting
        item {
            Column {
                Text(text = "Hey Flashback 👋", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Let's help you find what you're looking for.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Search
        item {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSearchClick()
                    },
                readOnly = true,
                placeholder = {
                    Text("Search lost or found items")
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Quick actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onReportLost,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TolaLostOrange
                    )
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Report Lost",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onReportFound,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TolaFoundTag
                    )
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(text = "Report Found", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Filter title
        item {
            Text(text = "Recent on Campus", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        // Filters
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "All Posts",
                    "Lost Only",
                    "Found Only"
                ).forEach { filter ->
                    AssistChip(
                        onClick = {
                            selectedFilter = filter
                        },
                        label = {
                            Text(filter)
                        }
                    )
                }
            }
        }

        // Feed
        items(items = filteredItems, key = { it.id }) { item ->
            LostFoundItemCard(
                item = item,
                onClick = {
                    onItemClick(item.id)
                }
            )
        }
    }
}

@Composable
private fun LostFoundItemCard(
    item: LostFoundFeedItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (item.isFound) {
                        TolaPrimaryContainer
                    } else {
                        Color(0xFFFFF7ED)
                    }
                ) {
                    Text(
                        text = if (item.isFound) "FOUND" else "LOST",
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isFound) {
                            TolaPrimary
                        } else {
                            TolaLostOrange
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = TolaPrimary
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(text = item.location, style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.width(14.dp))

                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(text = item.timeAgo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "#${item.tagNumber}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
