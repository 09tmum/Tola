package com.example.tola.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tola.lostfound.ui.theme.TolaBackground
import com.tola.lostfound.ui.theme.TolaBorder
import com.tola.lostfound.ui.theme.TolaPrimary
import com.tola.lostfound.ui.theme.TolaSurface
import com.tola.lostfound.ui.theme.TolaTextMuted
import com.tola.lostfound.ui.theme.TolaTextPrimary
import com.tola.lostfound.ui.theme.TolaTextSecondary

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
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Tola Admin", style = MaterialTheme.typography.labelLarge, color = TolaPrimary)
                Text(title, style = MaterialTheme.typography.headlineSmall, color = TolaTextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
            }
            if (onBack != null) {
                TextButton(onClick = onBack) { Text("Back", color = TolaPrimary) }
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
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
internal fun AdminEmptyState(message: String) {
    AdminPanelCard {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = TolaTextMuted)
    }
}
