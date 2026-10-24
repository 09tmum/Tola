package com.tola.lostfound.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tola.lostfound.ui.theme.*

@Composable
fun AdminClaimReviewScreen(
    claims: List<AdminClaim> = emptyList(),
    onBack: () -> Unit = {},
    onViewClaim: (AdminClaim) -> Unit = {},
    onApproveClaim: (AdminClaim) -> Unit = {},
    onRejectClaim: (AdminClaim) -> Unit = {}
) {
    AdminScreenLayout("Claim Review", "Review ownership claims before recording a decision.", onBack) {
        Text("${claims.count { it.status.equals("Pending", true) }} pending claim(s)", style = MaterialTheme.typography.labelLarge, color = TolaTextSecondary)
        if (claims.isEmpty()) {
            AdminEmptyState("No ownership claims are connected yet.")
        } else {
            claims.forEach { claim ->
                AdminPanelCard {
                    Text(claim.itemName, style = MaterialTheme.typography.titleMedium, color = TolaTextPrimary)
                    Text("Claim ID: ${claim.id}", style = MaterialTheme.typography.bodySmall, color = TolaTextMuted)
                    Text("Claimant: ${claim.claimantName}", style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
                    Text("Submitted: ${claim.submittedDate}", style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
                    AdminStatusChip(
                        label = claim.status.uppercase(),
                        background = TolaPrimaryContainer,
                        foreground = TolaOnPrimaryContainer
                    )
                    OutlinedButton(onClick = { onViewClaim(claim) }, shape = RoundedCornerShape(10.dp)) {
                        Text("View claim and evidence", color = TolaPrimary)
                    }
                    if (claim.status.equals("Pending", true)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onApproveClaim(claim) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TolaPrimary)
                            ) { Text("Approve") }
                            OutlinedButton(
                                onClick = { onRejectClaim(claim) },
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("Reject", color = TolaError) }
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
}

@Preview(name = "Admin Claim Review", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AdminClaimReviewPreview() {
    TolaTheme { AdminClaimReviewScreen(claims = previewAdminClaims) }
}
