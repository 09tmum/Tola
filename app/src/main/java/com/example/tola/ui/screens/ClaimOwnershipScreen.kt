package com.example.tola.ui.screens

import androidx.compose.ui.tooling.preview.Preview
import com.example.tola.ui.theme.TolaTheme
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import com.example.tola.data.ReportRepository
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.tola.data.ClaimRepository
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaPrimaryContainer
import kotlinx.coroutines.launch

@Composable
fun ClaimOwnershipScreen(
    itemId: String,
    onBack: () -> Unit,
    onClaimSubmitted: () -> Unit
) {
    var proofDescription by remember { mutableStateOf("") }
    var confirmed by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf<String?>(null) }
    var itemLocation by remember { mutableStateOf<String?>(null) }
    var isLoadingItem by remember { mutableStateOf(true) }
    var itemLoadError by remember { mutableStateOf<String?>(null) }

    val reportRepository = remember { ReportRepository() }

    LaunchedEffect(itemId) {
        isLoadingItem = true
        itemLoadError = null

        try {
            val report = reportRepository.getReportById(itemId)

            if (report != null && report.type == "FOUND") {
                itemName = report.itemName
                itemLocation = report.location
            } else {
                itemLoadError = "This found item could not be found."
            }
        } catch (e: Exception) {
            itemLoadError = e.localizedMessage
                ?: "Unable to load item details."
        } finally {
            isLoadingItem = false
        }
    }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val repository = remember { ClaimRepository() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val canSubmit =
        proofDescription.trim().isNotEmpty() && confirmed

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
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "Claim Ownership",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = "Is this your item?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Provide some information to help us verify your ownership.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Item summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                MaterialTheme.colorScheme.background
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📦",
                            fontSize = 28.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = itemName ?: if (isLoadingItem) {
                                "Loading item..."
                            } else {
                                "Item details unavailable"
                            },
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Item #$itemId",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = itemLocation ?: if (isLoadingItem) {
                                "Loading location..."
                            } else {
                                "Location unavailable"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security notice
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = TolaPrimaryContainer
                )
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
                            text = "Ownership verification",
                            fontWeight = FontWeight.Bold,
                            color = TolaPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Give details that only the real owner would know. " +
                                    "Do not share passwords or other sensitive information.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Proof description
            Text(
                text = "How can you prove it's yours?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Describe a unique feature, marking, sticker, contents, " +
                        "or other information about the item.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = proofDescription,
                onValueChange = {
                    if (it.length <= 400) {
                        proofDescription = it
                        errorMessage = null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                placeholder = {
                    Text(
                        "Example: It has a small scratch near the bottom..."
                    )
                },
                shape = RoundedCornerShape(12.dp),
                enabled = !isSubmitting
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${proofDescription.length}/400",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Photo/receipt UI — preserved from your original design
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TolaPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = null,
                            tint = TolaPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Supporting photo or receipt",
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Optional",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Campus credential
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TolaPrimary,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Campus verification",
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Your registered campus account may be used " +
                                    "to help verify your claim.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Confirmation checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = confirmed,
                    onCheckedChange = {
                        confirmed = it
                        errorMessage = null
                    },
                    enabled = !isSubmitting
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "I confirm that this item belongs to me and the " +
                            "information I provided is truthful.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submission error
            errorMessage?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Submit button — now connected to Firestore
            Button(
                onClick = {
                    if (isSubmitting) return@Button

                    coroutineScope.launch {
                        isSubmitting = true
                        errorMessage = null

                        try {
                            repository.submitClaim(
                                itemId = itemId,
                                proofDescription = proofDescription
                            )

                            Toast.makeText(
                                context,
                                "Claim submitted successfully!",
                                Toast.LENGTH_SHORT
                            ).show()

                            onClaimSubmitted()
                        } catch (e: Exception) {
                            errorMessage = e.localizedMessage
                                ?: "Unable to submit your claim. Please try again."
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TolaPrimary,
                    contentColor = Color.White
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Submitting...",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Submit Claim",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your claim will be reviewed before the item is returned.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClaimOwnershipScreenPreview() {
    TolaTheme {
        ClaimOwnershipScreen(
            itemId = "LF-1024",
            onBack = {},
            onClaimSubmitted = {}
        )
    }
}
