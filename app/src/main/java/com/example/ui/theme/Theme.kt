package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanNeon,
    onPrimary = Color.Black,
    primaryContainer = CyanNeonDark,
    onPrimaryContainer = Color.White,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF332900),
    onSecondaryContainer = GoldAccent,
    tertiary = EmeraldGreen,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF00381B),
    onTertiaryContainer = EmeraldGreen,
    error = CoralRed,
    onError = Color.Black,
    errorContainer = Color(0xFF3D0808),
    onErrorContainer = CoralRed,
    background = AmoledBlack,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder,
    outlineVariant = DarkSurfaceBorderHighlight
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // We enforce Pure AMOLED aesthetic as required by the user prompt
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
