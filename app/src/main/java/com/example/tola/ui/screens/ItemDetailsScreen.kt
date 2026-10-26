
package com.example.tola.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tola.data.LostFoundReport
import com.example.tola.data.ReportRepository
import com.example.tola.ui.theme.TolaFoundBackground
import com.example.tola.ui.theme.TolaFoundText
import com.example.tola.ui.theme.TolaLostBackground
import com.example.tola.ui.theme.TolaLostText
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaPrimaryContainer
import com.example.tola.ui.theme.TolaTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ItemDetailsScreen(
    itemId: String,
    onBack: () -> Unit,
    onClaimClick: (String) -> Unit,
    onReportFoundItemClick: ((String) -> Unit)? = null
) {
    val repository = remember { ReportRepository() }

    var report by remember {
        mutableStateOf<LostFoundReport?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(itemId) {
        isLoading = true
        errorMessage = null

        try {
            report = repository.getReportById(itemId)

            if (report == null) {
                errorMessage = "This item could not be found."
            }
        } catch (e: Exception) {
            errorMessage = e.localizedMessage
                ?: "Unable to load item details."
        } finally {
            isLoading = false
        }
    }

    val item = report

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
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
            if (!isLoading && item != null) {
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
                            onClick = { /* Sharing can be added later. */ },
                            modifier = Modifier
                                .weight(0.35f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                    MaterialTheme.colorScheme.surfaceVariant,
                                contentColor =
                                    MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share"
                            )
                        }

                        Button(
                            onClick = {
                                if (item.type == "FOUND") {
                                    onClaimClick(item.id)
                                } else {
                                    onReportFoundItemClick?.invoke(item.id)
                                        ?: onClaimClick(item.id)
                                }
                            },
                            modifier = Modifier
                                .weight(0.65f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TolaPrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (item.type == "FOUND") {
                                    "Claim This Item"
                                } else {
                                    "I Found This Item"
                                },
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
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
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Loading item details...")
                        }
                    }
                }

                errorMessage != null -> {
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = errorMessage ?: "Unable to load this item.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = onBack) {
                        Text("Go Back")
                    }
                }

                item != null -> {
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

                    // Lost/found status
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (item.type == "FOUND") {
                            TolaFoundBackground
                        } else {
                            TolaLostBackground
                        }
                    ) {
                        Text(
                            text = item.type,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.type == "FOUND") {
                                TolaFoundText
                            } else {
                                TolaLostText
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = item.itemName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = item.description.ifBlank {
                            "No description provided."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category and date/time chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoChip(
                            icon = Icons.Default.Category,
                            text = item.category.ifBlank { "Uncategorised" }
                        )

                        InfoChip(
                            icon = Icons.Default.Schedule,
                            text = listOf(item.date, item.time)
                                .filter { it.isNotBlank() }
                                .joinToString(" · ")
                                .ifBlank { "Time not provided" }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Location
                    DetailSection(
                        title = if (item.type == "FOUND") {
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
                                text = item.location.ifBlank {
                                    "Location not provided"
                                },
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Description
                    DetailSection(title = "Description") {
                        Text(
                            text = item.description.ifBlank {
                                "No additional details provided."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Additional information
                    DetailSection(
                        title = if (item.type == "FOUND") {
                            "Finder's comment"
                        } else {
                            "Additional information"
                        }
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (item.type == "FOUND") {
                                    "This item was reported as found."
                                } else {
                                    "If you find this item, please report it through Tola."
                                },
                                modifier = Modifier.padding(14.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Safety notice
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
                                    text = "Do not share sensitive personal information. " +
                                            "Use Tola's claim process to verify ownership.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Report reference: ${item.id.takeLast(6)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
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
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        content()
    }
}

@Preview(showBackground = true)
@Composable
fun ItemDetailsScreenPreview() {
    TolaTheme {
        ItemDetailsScreen(
            itemId = "sample-document-id",
            onBack = {},
            onClaimClick = {}
        )
    }
}
