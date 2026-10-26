package com.example.tola.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.tola.ui.theme.TolaError
import com.example.tola.ui.theme.TolaLostBackground
import com.example.tola.ui.theme.TolaLostText
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaPrimaryContainer
import com.example.tola.ui.theme.TolaTextMuted
import com.example.tola.ui.theme.TolaTextPrimary
import com.example.tola.ui.theme.TolaTextSecondary
import com.example.tola.ui.theme.TolaTheme

@Composable
fun AdminClaimReviewScreen(

    claims: List<AdminClaim> = emptyList(),
    onBack: () -> Unit = {},
    onViewClaim: (AdminClaim) -> Unit = {},
    onApproveClaim: (AdminClaim) -> Unit = {},
    onRejectClaim: (AdminClaim) -> Unit = {}
) {
    var selectedClaim by remember {
        mutableStateOf<AdminClaim?>(null)
    }

    var actionMessage by remember {
        mutableStateOf<String?>(null)
    }
    AdminScreenLayout("Claim Review", "Review ownership claims before recording a decision.", onBack) {
        Text("${claims.count { it.status.equals("Pending", true) }} pending claim(s)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TolaTextSecondary)
        if (claims.isEmpty()) {
            AdminEmptyState("No ownership claims are connected yet.")
        } else {
            claims.forEach { claim ->
                AdminPanelCard {
                    Text(claim.itemName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TolaTextPrimary)
                    Text("Claim ID: ${claim.id}", style = MaterialTheme.typography.bodySmall, color = TolaTextMuted)
                    Text("Claimant: ${claim.claimantName}", style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
                    Text("Submitted: ${claim.submittedDate}", style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
                    AdminStatusChip(
                        label = claim.status.uppercase(),
                        background = if (claim.status.equals("Pending", true)) TolaLostBackground else TolaPrimaryContainer,
                        foreground = if (claim.status.equals("Pending", true)) TolaLostText else TolaPrimary
                    )
                    OutlinedButton(
                        onClick = {
                            selectedClaim = claim
                            onViewClaim(claim)
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            "View claim and evidence",
                            color = TolaPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (claim.status.equals("Pending", true)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    onApproveClaim(claim)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TolaPrimary,
                                    contentColor = Color.White
                                )
                            ) { Text("Approve", fontWeight = FontWeight.Bold, color = Color.White) }
                            OutlinedButton(
                                onClick = {
                                    onRejectClaim(claim)
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("Reject", color = TolaError, fontWeight = FontWeight.SemiBold) }
                        }
                    }
                }
            }
        }
        Text(
            "Only approve a claim after checking sufficient ownership evidence. A submitted claim is not proof of ownership.",
            style = MaterialTheme.typography.bodySmall,
            color = TolaTextMuted
        )
    }
    selectedClaim?.let { claim ->
        AlertDialog(
            onDismissRequest = {
                selectedClaim = null
            },
            title = {
                Text("Claim details")
            },
            text = {
                androidx.compose.foundation.layout.Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Item: ${claim.itemName}")
                    Text("Claim ID: ${claim.id}")
                    Text("Claimant: ${claim.claimantName}")
                    Text("Submitted: ${claim.submittedDate}")
                    Text("Status: ${claim.status}")

                    Text(
                        "Ownership evidence",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        claim.proofDescription.ifBlank {
                            "No ownership evidence was provided."
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedClaim = null
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }

    actionMessage?.let { message ->
        AlertDialog(
            onDismissRequest = {
                actionMessage = null
            },
            title = {
                Text("Claim review")
            },
            text = {
                Text(message)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        actionMessage = null
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview(name = "Admin Claim Review", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AdminClaimReviewPreview() {
    TolaTheme { AdminClaimReviewScreen(claims = previewAdminClaims) }
}
