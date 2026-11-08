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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import android.widget.Toast
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tola.auth.FirebaseAuthRepository
import com.example.tola.auth.UserProfile
import com.example.tola.data.ClaimRepository
import com.example.tola.data.FirebaseStorageRepository
import com.example.tola.data.LostFoundReport
import com.example.tola.data.ReportRepository
import com.example.tola.ui.theme.TolaFoundBackground
import com.example.tola.ui.theme.TolaFoundText
import com.example.tola.ui.theme.TolaLostBackground
import com.example.tola.ui.theme.TolaLostText
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaPrimaryContainer
import com.example.tola.ui.theme.TolaTheme

@Composable
fun ProfileScreen(
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {},
    onLogout: () -> Unit
) {
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var userReports by remember { mutableStateOf<List<LostFoundReport>>(emptyList()) }
    var pendingClaimsCount by remember { mutableStateOf(0) }

    var showEditDialog by remember { mutableStateOf(false) }
    var editFullName by remember { mutableStateOf("") }
    var editPhoneNumber by remember { mutableStateOf("") }
    var editProfilePictureUrl by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val profilePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val mimeType = context.contentResolver.getType(uri)
            if (mimeType == null || mimeType.startsWith("image/")) {
                editProfilePictureUrl = uri.toString()
                Toast.makeText(context, "Photo selected! Click Save to apply.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Invalid file type. Please select an image file (.jpg, .png).", Toast.LENGTH_LONG).show()
            }
        }
    }

    val isPreview = LocalInspectionMode.current
    val authRepository = if (isPreview) null else remember { FirebaseAuthRepository() }
    val reportRepository = if (isPreview) null else remember { ReportRepository() }
    val claimRepository = if (isPreview) null else remember { ClaimRepository() }

    LaunchedEffect(isPreview) {
        if (isPreview) {
            userProfile = UserProfile(
                fullName = "Flashback Prince",
                email = "flashback.prince@campus.edu",
                studentId = "STU-10293"
            )
            userReports = listOf(
                LostFoundReport(
                    id = "LF-1023",
                    itemName = "North Face Recon Backpack",
                    location = "Computer Lab",
                    type = "LOST",
                    status = "OPEN"
                )
            )
            pendingClaimsCount = 1
            return@LaunchedEffect
        }
        userProfile = authRepository?.getCurrentUserProfile()
        userReports = reportRepository?.getUserReports().orEmpty()
        pendingClaimsCount = claimRepository?.getUserPendingClaimsCount() ?: 0
    }

    val activeReportsCount = userReports.count { it.status == "OPEN" }
    val itemsReturnedCount = userReports.count { it.status == "RESOLVED" || it.status == "RETURNED" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        item {
            Text(
                text = "My Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Profile card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = TolaPrimary
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile picture / Initials
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (userProfile?.profilePictureUrl?.isNotBlank() == true) {
                            AsyncImage(
                                model = userProfile?.profilePictureUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = userProfile?.initials ?: "U",
                                color = TolaPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userProfile?.fullName?.ifBlank { "Campus Student" } ?: "Campus Student",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = userProfile?.email?.ifBlank { "No email registered" } ?: "No email registered",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            editFullName = userProfile?.fullName.orEmpty()
                            editPhoneNumber = userProfile?.phoneNumber.orEmpty()
                            nameError = false
                            showEditDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Real Statistics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatCard(
                    number = activeReportsCount.toString(),
                    label = "Active Reports",
                    modifier = Modifier.weight(1f)
                )

                ProfileStatCard(
                    number = pendingClaimsCount.toString(),
                    label = "Pending Claims",
                    modifier = Modifier.weight(1f)
                )

                ProfileStatCard(
                    number = itemsReturnedCount.toString(),
                    label = "Items Returned",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // My Reports header
        item {
            Text(
                text = "My Reports",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // My Reports list
        if (userReports.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "You haven't submitted any reports yet.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(items = userReports, key = { it.id }) { report ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(report.id) },
                    shape = RoundedCornerShape(16.dp)
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
                                    text = report.itemName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = "Reported at ${report.location}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (report.type == "FOUND") TolaFoundBackground else TolaLostBackground
                            ) {
                                Text(
                                    text = report.type,
                                    modifier = Modifier.padding(
                                        horizontal = 9.dp,
                                        vertical = 5.dp
                                    ),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (report.type == "FOUND") TolaFoundText else TolaLostText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (report.status == "OPEN") "Active report" else "Closed report",
                                color = if (report.type == "FOUND") TolaFoundText else TolaLostText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = "#${report.id.takeLast(6)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Settings header
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Notifications row with onClick navigation
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNotificationsClick() },
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = TolaPrimary
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Notifications",
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Get notified about item matches and claims.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null
                    )
                }
            }
        }

        // Account information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = TolaPrimaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TolaPrimary
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (userProfile?.studentId?.isNotBlank() == true) {
                                "Student ID: ${userProfile?.studentId}"
                            } else {
                                "Campus Account"
                            },
                            fontWeight = FontWeight.Bold,
                            color = TolaPrimary
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = if (userProfile?.phoneNumber?.isNotBlank() == true) {
                                "Phone: ${userProfile?.phoneNumber}"
                            } else {
                                "Your campus identity helps verify ownership claims."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Logout
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onLogout()
                    },
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFFF1F2)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = Color(0xFFDC2626)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Log Out",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFDC2626)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tola • Campus Lost & Found",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editFullName,
                        onValueChange = {
                            editFullName = it
                            if (it.isNotBlank()) nameError = false
                        },
                        label = { Text("Full Name *") },
                        isError = nameError,
                        supportingText = {
                            if (nameError) {
                                Text("Full name cannot be empty", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editPhoneNumber,
                        onValueChange = { editPhoneNumber = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Profile Photo option
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                profilePhotoPickerLauncher.launch("image/*")
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = TolaPrimary
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = if (editProfilePictureUrl.isNotBlank()) "Photo attached" else "Change Profile Photo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = if (editProfilePictureUrl.isNotBlank()) "Click to change photo" else "Upload photo from gallery",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editFullName.isBlank()) {
                            nameError = true
                            return@TextButton
                        }
                        coroutineScope.launch {
                            try {
                                val storageRepo = FirebaseStorageRepository()
                                val cloudProfileUrl = if (editProfilePictureUrl.isNotBlank() && editProfilePictureUrl.startsWith("content:")) {
                                    storageRepo.uploadImage(context, Uri.parse(editProfilePictureUrl), "profiles")
                                } else {
                                    editProfilePictureUrl
                                }

                                authRepository?.updateUserProfile(
                                    fullName = editFullName,
                                    phoneNumber = editPhoneNumber,
                                    profilePictureUrl = cloudProfileUrl
                                )
                                userProfile = authRepository?.getCurrentUserProfile()
                                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                showEditDialog = false
                            } catch (e: Exception) {
                                Toast.makeText(context, e.localizedMessage ?: "Failed to update profile", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileStatCard(
    number: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TolaPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    TolaTheme {
        ProfileScreen(
            onHomeClick = {},
            onSearchClick = {},
            onNotificationsClick = {},
            onItemClick = {},
            onLogout = {}
        )
    }
}
