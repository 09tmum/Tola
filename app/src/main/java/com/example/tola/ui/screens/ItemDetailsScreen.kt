package com.example.tola.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaPrimaryContainer
import com.example.tola.ui.theme.TolaSuccessGreen

@Composable
fun ItemDetailsScreen(
    itemId: String,
    onBack: () -> Unit,
    onClaimClick: (String) -> Unit
) {

    // Temporary item data.
    // Later this can come from your database/backend.
    val item = when (itemId) {

        "LF-1" -> ItemDetailData(
            id = "LF-1024",
            title = "Cobalt Blue Hydro Flask",
            category = "Accessories",
            status = "FOUND",
            time = "15 minutes ago",
            location = "Main Library",
            description = "Blue Hydro Flask water bottle with a black lid.",
            details = "The bottle has a small scratch near the bottom and a black lid.",
            finderComment = "Found on a table near the library entrance."
        )

        "LF-2" -> ItemDetailData(
            id = "LF-1023",
            title = "North Face Recon Backpack",
            category = "Bags",
            status = "LOST",
            time = "1 hour ago",
            location = "Computer Lab",
            description = "Black North Face backpack with a small red keychain.",
            details = "The backpack contains several books and has a red keychain attached.",
            finderComment = "Please contact the owner if you have seen this bag."
        )

        "LF-3" -> ItemDetailData(
            id = "LF-1022",
            title = "AirPods Pro with Red Case",
            category = "Electronics",
            status = "FOUND",
            time = "2 hours ago",
            location = "Student Centre",
            description = "White AirPods in a red protective case.",
            details = "The case is red and has a small mark on the back.",
            finderComment = "Found near the student centre seating area."
        )

        else -> ItemDetailData(
            id = itemId,
            title = "Student ID Card",
            category = "IDs",
            status = "LOST",
            time = "3 hours ago",
            location = "Main Car Park",
            description = "University student identification card.",
            details = "Please provide identifying information when making a claim.",
            finderComment = "If found, please report it through Tola."
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "Item Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },

        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(0.35f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }

                    Button(
                        onClick = {
                            onClaimClick(item.id)
                        },
                        modifier = Modifier
                            .weight(0.65f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TolaPrimary
                        )
                    ) {
                        Text(
                            text = if (item.status == "FOUND") {
                                "Claim This Item"
                            } else {
                                "I Found This Item"
                            },
                            fontWeight = FontWeight.Bold
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp)
        ) {

            // Image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Item Photo",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (item.status == "FOUND") {
                    TolaSuccessGreen.copy(alpha = 0.12f)
                } else {
                    Color(0xFFFFF7ED)
                }
            ) {
                Text(
                    text = item.status,
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.status == "FOUND") {
                        TolaSuccessGreen
                    } else {
                        Color(0xFFF97316)
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Information chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                InfoChip(
                    icon = Icons.Default.Category,
                    text = item.category
                )

                InfoChip(
                    icon = Icons.Default.Schedule,
                    text = item.time
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Discovery location
            DetailSection(
                title = if (item.status == "FOUND") {
                    "Where it was found"
                } else {
                    "Where it was lost"
                }
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TolaPrimary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = item.location,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Item details
            DetailSection(
                title = "Description"
            ) {

                Text(
                    text = item.details,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Finder comment
            DetailSection(
                title = if (item.status == "FOUND") {
                    "Finder's comment"
                } else {
                    "Additional information"
                }
            ) {

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = item.finderComment,
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security notice
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = TolaPrimaryContainer
                ),
                shape = RoundedCornerShape(14.dp)
            ) {

                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {

                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = TolaPrimary,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {

                        Text(
                            text = "Stay safe",
                            fontWeight = FontWeight.Bold,
                            color = TolaPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Do not share sensitive personal information. "
                                    + "Use Tola's claim process to verify ownership.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class ItemDetailData(
    val id: String,
    val title: String,
    val category: String,
    val status: String,
    val time: String,
    val location: String,
    val description: String,
    val details: String,
    val finderComment: String
)

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 7.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = text,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        content()
    }
}
