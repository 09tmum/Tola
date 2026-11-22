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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tola.data.ReportRepository
import com.example.tola.data.LostFoundReport
import com.example.tola.data.NotificationRepository
import com.example.tola.ui.theme.TolaBackground
import com.example.tola.ui.theme.TolaBorder
import com.example.tola.ui.theme.TolaFoundBackground
import com.example.tola.ui.theme.TolaFoundText
import com.example.tola.ui.theme.TolaLostBackground
import com.example.tola.ui.theme.TolaLostText
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaPrimaryContainer
import com.example.tola.ui.theme.TolaSurface
import com.example.tola.ui.theme.TolaTheme

@Composable
fun SearchScreen(
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onReportLost: () -> Unit = {},
    onItemClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("All") }
    var selectedCategory by remember { mutableStateOf("All") }

    var searchItems by remember { mutableStateOf<List<LostFoundFeedItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    val isPreview = LocalInspectionMode.current
    val repository = if (isPreview) null else remember { ReportRepository() }

    LaunchedEffect(isPreview) {
        if (isPreview) {
            searchItems = listOf(
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
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        try {
            val reports = repository?.getReports().orEmpty()
            searchItems = reports.map { report ->
                val date = listOf(report.date, report.time).filter { it.isNotBlank() }.joinToString(" · ")
                LostFoundFeedItem(
                    id = report.id,
                    title = report.itemName,
                    description = report.description,
                    location = report.location,
                    timeAgo = date.ifBlank { "Recently" },
                    isFound = report.type == "FOUND",
                    tagNumber = report.id.takeLast(6),
                    imageUrl = report.imageUrl
                )
            }
            loadError = null
        } catch (e: Exception) {
            loadError = e.localizedMessage ?: "Unable to load search items."
        } finally {
            isLoading = false
        }
    }

    val filteredItems = searchItems.filter { item ->
        val matchesSearch =
            searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.location.contains(searchQuery, ignoreCase = true)

        val matchesType = when (selectedType) {
            "Found" -> item.isFound
            "Lost" -> !item.isFound
            else -> true
        }

        val matchesCategory = when (selectedCategory) {
            "All" -> true
            else -> item.title.contains(selectedCategory, ignoreCase = true) ||
                    item.description.contains(selectedCategory, ignoreCase = true)
        }

        matchesSearch && matchesType && matchesCategory
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TolaBackground),
        contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Search", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Find lost and found items on campus.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = {}
                    ) {
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filters")
                    }
                }
            }

            // Search field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = {
                        Text("Search items...")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp)
                )
            }

            // Lost / Found tabs
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "All",
                        "Found",
                        "Lost"
                    ).forEach { type ->

                        AssistChip(onClick = {
                                selectedType = type
                            },
                            label = { Text(type) }
                        )
                    }
                }
            }

            // Categories
            item {
                Column {

                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "All",
                            "Electronics",
                            "Bags"
                        ).forEach { category ->

                            AssistChip(onClick = { selectedCategory = category },
                                label = { Text(category) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "IDs",
                            "Accessories"
                        ).forEach { category ->

                            AssistChip(onClick = { selectedCategory = category },
                                label = { Text(category) }
                            )
                        }
                    }
                }
            }

            // Results heading
            item {
                Text(
                    text = if (isLoading) "Loading search items..." else "${filteredItems.size} items found",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                // Results
                items(
                    items = filteredItems,
                    key = { it.id }
                ) { item ->

                    SearchItemCard(
                        item = item,
                        onClick = {
                            onItemClick(item.id)
                        }
                    )
                }

                // No results
                if (filteredItems.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = TolaPrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "No items found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Try a different search or category.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Alert banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onReportLost),
                    colors = CardDefaults.cardColors(
                        containerColor = TolaPrimaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = TolaPrimary,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(text = "Can't spot your item?", fontWeight = FontWeight.Bold, color = TolaPrimary)

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "Report it as lost so other students can help.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
}

@Composable
private fun SearchItemCard(
    item: LostFoundFeedItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TolaBorder),
        colors = CardDefaults.cardColors(
            containerColor = TolaSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

                Spacer(modifier = Modifier.width(10.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (item.isFound) {
                        TolaFoundBackground
                    } else {
                        TolaLostBackground
                    }
                ) {
                    Text(
                        text = if (item.isFound) "FOUND" else "LOST",
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 5.dp
                        ),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isFound) {
                            TolaFoundText
                        } else {
                            TolaLostText
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                    tint = TolaPrimary
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(text = item.location, style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.width(12.dp))

                Text(text = item.timeAgo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "#${item.tagNumber}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    TolaTheme {
        SearchScreen(onItemClick = {})
    }
}
