package com.example.tola.ui.screens

import androidx.compose.ui.tooling.preview.Preview
import com.example.tola.ui.theme.TolaTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tola.ui.theme.TolaBackground
import com.example.tola.ui.theme.TolaBorder
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaSurface
import com.example.tola.ui.theme.TolaTextMuted
import com.example.tola.ui.theme.TolaTextPrimary
import com.example.tola.ui.theme.TolaTextSecondary

@Composable
internal fun AdminScreenLayout(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TolaBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TolaTextPrimary
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text("Tola Admin", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TolaPrimary)
                Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TolaTextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
            }
        }
        content()
    }
}

@Composable
internal fun AdminPanelCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TolaBorder),
        colors = CardDefaults.cardColors(containerColor = TolaSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
internal fun AdminStatusChip(
    label: String,
    background: Color,
    foreground: Color
) {
    Surface(
        color = background,
        contentColor = foreground,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun AdminEmptyState(message: String) {
    AdminPanelCard {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = TolaTextMuted)
    }
}

@Preview(showBackground = true)
@Composable
fun AdminUiComponentsPreview() {
    TolaTheme {
        AdminScreenLayout(
            title = "Admin Component Preview",
            subtitle = "Overview of admin UI components"
        ) {
            AdminPanelCard {
                Text("Panel Card Content")
                AdminStatusChip(label = "Active", background = Color(0xFFDCFCE7), foreground = Color(0xFF166534))
            }
            AdminEmptyState("No items available.")
        }
    }
}
