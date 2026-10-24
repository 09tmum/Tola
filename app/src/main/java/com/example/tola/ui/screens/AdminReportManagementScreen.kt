package com.example.tola.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tola.ui.theme.TolaError
import com.example.tola.ui.theme.TolaFoundBackground
import com.example.tola.ui.theme.TolaFoundText
import com.example.tola.ui.theme.TolaLostBackground
import com.example.tola.ui.theme.TolaLostText
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaTextMuted
import com.example.tola.ui.theme.TolaTextPrimary
import com.example.tola.ui.theme.TolaTextSecondary
import com.example.tola.ui.theme.TolaTheme

@Composable
fun AdminReportManagementScreen(
    reports: List<AdminItemReport> = emptyList(),
    onBack: () -> Unit = {},
    onViewReport: (AdminItemReport) -> Unit = {},
    onMarkRecovered: (AdminItemReport) -> Unit = {},
    onRemoveReport: (AdminItemReport) -> Unit = {}
) {
    var filter by rememberSaveable { mutableStateOf("All") }
    val visibleReports = reports.filter { report ->
        when (filter) {
            "Lost" -> report.reportType.equals("Lost", true)
            "Found" -> report.reportType.equals("Found", true)
            "Recovered" -> report.status.equals("Recovered", true)
            else -> true
        }
    }

    AdminScreenLayout("Report Management", "Review reports and update item status.", onBack) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("All", "Lost", "Found", "Recovered").forEach { option ->
                FilterChip(
                    selected = filter == option,
                    onClick = { filter = option },
                    label = { Text(option) }
                )
            }
        }
        Text("${visibleReports.size} report(s)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TolaTextSecondary)
        if (visibleReports.isEmpty()) {
            AdminEmptyState(if (reports.isEmpty()) "No item reports are connected yet." else "No reports match this filter.")
        } else {
            visibleReports.forEach { report ->
                AdminPanelCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(report.itemName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TolaTextPrimary, modifier = Modifier.weight(1f))
                        AdminStatusChip(
                            label = report.reportType.uppercase(),
                            background = if (report.reportType.equals("Found", true)) TolaFoundBackground else TolaLostBackground,
                            foreground = if (report.reportType.equals("Found", true)) TolaFoundText else TolaLostText
                        )
                    }
                    Text("Report ID: ${report.id}", style = MaterialTheme.typography.bodySmall, color = TolaTextMuted)
                    Text("Reported by: ${report.reporterName}", style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
                    Text("Location: ${report.location}", style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
                    Text("Status: ${report.status}", style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { onViewReport(report) }, shape = RoundedCornerShape(10.dp)) {
                            Text("View", color = TolaPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        if (!report.status.equals("Recovered", true)) {
                            Button(
                                onClick = { onMarkRecovered(report) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TolaPrimary,
                                    contentColor = Color.White
                                )
                            ) { Text("Mark recovered", fontWeight = FontWeight.Bold, color = Color.White) }
                        }
                    }
                    OutlinedButton(
                        onClick = { onRemoveReport(report) },
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Remove report", color = TolaError, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}

@Preview(name = "Admin Report Management", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AdminReportManagementPreview() {
    TolaTheme { AdminReportManagementScreen(reports = previewAdminReports) }
}
