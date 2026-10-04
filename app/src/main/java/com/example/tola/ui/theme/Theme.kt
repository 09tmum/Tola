package com.example.tola.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val TolaShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)
private val LightColorScheme = lightColorScheme(
    primary = TolaPrimary,
    onPrimary = Color.White,

    primaryContainer = TolaPrimaryContainer,
    onPrimaryContainer = TolaOnPrimaryContainer,

    background = TolaBackground,
    surface = TolaSurface,
    surfaceVariant = TolaSurfaceVariant,

    onBackground = TolaTextPrimary,
    onSurface = TolaTextPrimary,

    outline = TolaBorder
)

@Composable
fun TolaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = TolaTypography,
        shapes = TolaShapes,
        content = content
    )
}